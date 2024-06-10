package com.juke.api.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import com.juke.api.dto.PaymentDTO;
import com.juke.api.model.TrackOrder;
import com.juke.api.utils.SystemLogger;

public class MercadoPagoPaymentGatewayImpl implements IPaymentGateway {

	@Override
	public void process(PaymentDTO paymentDTO) throws Exception {
		// handled in the client
	}

	@Override
	public String generatePaymentId(PaymentDTO paymentDTO, TrackOrder order) throws Exception {
		try {
			//ID creation
			String externalReference = UUID.randomUUID().toString();
			Map<String, Object> preference = buildPreference(paymentDTO, externalReference);
			//updating order
			order.setExternalReference(externalReference);
			
			return getPreferenceId(preference, paymentDTO);
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
	}

	private Map<String, Object> buildPreference(PaymentDTO paymentDTO, String externalReference) throws Exception {

		Map<String, Object> preference = new HashMap<>();

		preference.put("items", List.of(Map.of("title", paymentDTO.getDescription(), "unit_price",
				Double.parseDouble(paymentDTO.getPrice().toString()), "quantity", paymentDTO.getQuantity()// ,
		// "currency_id", paymentDTO.getCurrency() consider currency set on MP account
		)));

		preference.put("back_urls",
				Map.of("success", paymentDTO.getSuccessUrl(), "failure", paymentDTO.getFailedUrl(), "pending", ""));
		preference.put("auto_return", "approved");
		preference.put("marketplace_fee", calculateFee(paymentDTO.getPrice(), paymentDTO.getMarketplaceFee()));
		preference.put("notification_url", paymentDTO.getWebHookUrl());
		preference.put("external_reference", externalReference);

		return preference;

	}

	private String sanitizeForUrl(String input) {
		return input.replaceAll("'", "");
	}

	private String getPreferenceId(Map<String, Object> preference, PaymentDTO paymentDTO) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map> response = restTemplate.postForEntity(
				"https://api.mercadopago.com/checkout/preferences?access_token=" + paymentDTO.getToken(), preference,
				Map.class);

		Map<String, Object> responseBody = response.getBody();
		if (responseBody != null && response.getStatusCode() == HttpStatus.CREATED) {
			return (String) responseBody.get("id");
		} else {
			throw new Exception(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
	}
	
	private BigDecimal calculateFee(Double amount, Double marketplaceFee) {
		BigDecimal price = new BigDecimal(amount);
		BigDecimal fee = new BigDecimal(marketplaceFee);
		return price.multiply(fee);
	}
	
	@Override
	public HttpStatusCode refundCash(String paymentId, String token, TrackOrder order) throws Exception {
	    
	    RestTemplate restTemplate = new RestTemplate();
	    Map<String, Object> refundData = new HashMap<>();
	    refundData.put("amount", order.getAmount());
	    refundData.put("reason", "Error inesperado al encolar canción " + order.getTrack().getDescription());

	    HttpHeaders headers = new HttpHeaders();
	    headers.add("X-Idempotency-Key", UUID.randomUUID().toString()); // Generate a unique idempotency key

	    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(refundData, headers);

	    ResponseEntity<Map> response = restTemplate.postForEntity(
	        "https://api.mercadopago.com/v1/payments/" + paymentId + "/refunds?access_token=" + token,
	        requestEntity,
	        Map.class
	    );

	    return response.getStatusCode();
	}

}
