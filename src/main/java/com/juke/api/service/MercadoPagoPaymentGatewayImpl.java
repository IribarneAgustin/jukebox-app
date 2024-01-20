package com.juke.api.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juke.api.dto.PaymentDTO;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.payment.PaymentAdditionalInfoPayerRequest;
import com.mercadopago.client.payment.PaymentAdditionalInfoRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentItemRequest;
import com.mercadopago.client.payment.PaymentOrderRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.payment.PaymentReceiverAddressRequest;
import com.mercadopago.client.payment.PaymentShipmentsRequest;
import com.mercadopago.client.paymentmethod.PaymentMethodClient;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

public class MercadoPagoPaymentGatewayImpl implements IPaymentGateway {

	@Override
	public void process(PaymentDTO paymentDTO) throws Exception {
		// handled in the client
	}

	@Override
	public String generatePaymentId(PaymentDTO paymentDTO) throws Exception {
		try {
			Map<String, Object> preference = buildPreference(paymentDTO);
			//applyServiceFee(paymentDTO);
			String paymentId = getPreferenceId(preference, paymentDTO);

			return paymentId;

		} catch (Exception e) {
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
		preference.put("marketplace_fee", 300); //TODO get it from env

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

}
