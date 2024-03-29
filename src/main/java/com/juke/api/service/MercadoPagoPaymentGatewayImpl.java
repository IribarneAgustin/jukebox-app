package com.juke.api.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import com.juke.api.dto.PaymentDTO;
import com.juke.api.utils.SystemLogger;

public class MercadoPagoPaymentGatewayImpl implements IPaymentGateway {

	@Override
	public void process(PaymentDTO paymentDTO) throws Exception {
		// handled in the client
	}

	@Override
	public String generatePaymentId(PaymentDTO paymentDTO) throws Exception {
		try {
			Map<String, Object> preference = buildPreference(paymentDTO);
			String paymentId = getPreferenceId(preference, paymentDTO);
			return paymentId;
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
	}

	private Map<String, Object> buildPreference(PaymentDTO paymentDTO) throws Exception {

		Map<String, Object> preference = new HashMap<>();

		preference.put("items", List.of(Map.of("title", paymentDTO.getDescription(), "unit_price",
				Double.parseDouble(paymentDTO.getPrice().toString()), "quantity", paymentDTO.getQuantity()// ,
		// "currency_id", paymentDTO.getCurrency() consider currency set on MP account
		)));

		preference.put("back_urls",
				Map.of("success", buildSuccessUrl(paymentDTO), "failure", paymentDTO.getFailedUrl(), "pending", ""));
		preference.put("auto_return", "approved");
		preference.put("marketplace_fee", calculateFee(paymentDTO.getPrice(), paymentDTO.getMarketplaceFee()));

		return preference;

	}

	private String buildSuccessUrl(PaymentDTO paymentDTO) throws Exception {

		return paymentDTO.getSuccessUrl() + "?"
				+ new StringJoiner("&").add("trackURI=" + paymentDTO.getTrackInfoDTO().getTrackUri())
						.add("amount=" + paymentDTO.getPrice().toString())
						.add("albumCover=" + paymentDTO.getTrackInfoDTO().getAlbumCover())
						.add("artistName=" + sanitizeForUrl(paymentDTO.getTrackInfoDTO().getArtistName()))
						.add("trackName=" + sanitizeForUrl(paymentDTO.getTrackInfoDTO().getTrackName())).toString();
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

}
