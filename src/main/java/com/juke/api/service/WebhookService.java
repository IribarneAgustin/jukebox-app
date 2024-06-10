package com.juke.api.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juke.api.utils.SystemLogger;

import io.jsonwebtoken.security.InvalidKeyException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class WebhookService {
	
    @Value("${MERCADO_PAGO_WEBHOOK_SECRET_KEY}")
    private String secretKey;
    
	private WebClient webClient;
	
	@Autowired
	private MercadoPagoAuthService mercadoPagoAuthService;

	public WebhookService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("https://api.mercadopago.com").build();
	}

	public boolean validateSignature(HttpServletRequest request)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException, java.security.InvalidKeyException {
		String xSignature = request.getHeader("x-signature");
		if (xSignature == null) {
			return false;
		}

		String xRequestId = request.getHeader("x-request-id");
		Map<String, String> queryParams = getQueryParams(request);

		String dataId = queryParams.get("data.id");

		String[] parts = xSignature.split(",");
		String ts = null;
		String v1 = null;

		for (String part : parts) {
			String[] keyValue = part.split("=");
			if (keyValue.length == 2) {
				if (keyValue[0].trim().equals("ts")) {
					ts = keyValue[1].trim();
				} else if (keyValue[0].trim().equals("v1")) {
					v1 = keyValue[1].trim();
				}
			}
		}

		if (ts == null || v1 == null) {
			return false;
		}

		String manifest = String.format("id:%s;request-id:%s;ts:%s;", dataId, xRequestId, ts);

		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
		mac.init(secretKeySpec);
		byte[] hmacData = mac.doFinal(manifest.getBytes());

		StringBuilder sb = new StringBuilder();
		for (byte b : hmacData) {
			sb.append(String.format("%02x", b));
		}

		String generatedHash = sb.toString();

		return generatedHash.equals(v1);
	}

	private Map<String, String> getQueryParams(HttpServletRequest request) {
		Map<String, String> queryParams = new HashMap<>();
		String queryString = request.getQueryString();
		if (queryString != null) {
			String[] params = queryString.split("&");
			for (String param : params) {
				String[] keyValue = param.split("=");
				if (keyValue.length == 2) {
					queryParams.put(keyValue[0], keyValue[1]);
				}
			}
		}
		return queryParams;
	}

	public Map<String, Object> getPaymentIfApproved(String paymentId) {
	    try {
		    String url = String.format("/v1/payments/%s?access_token=%s", paymentId, mercadoPagoAuthService.getToken());
	        ResponseEntity<Map> response = webClient.get().uri(url).retrieve().toEntity(Map.class).block();

	        if (response != null) {
	            if (response.getStatusCode().is2xxSuccessful()) {
	                Map<String, Object> paymentData = response.getBody();
	                if (paymentData != null) {
	                    String status = (String) paymentData.get("status");
	                    if ("approved".equals(status)) {
	                        return paymentData;
	                    }
	                }
	            } else {
	                SystemLogger.info("Received non-success response from Mercado Pago API: " + response.getStatusCode());
	            }
	        }
	    } catch (WebClientResponseException e) {
	        // This exception is thrown when a 4xx or 5xx response is received
	        SystemLogger.error("Error response from Mercado Pago API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
	    } catch (Exception e) {
	        SystemLogger.error("Exception occurred while calling Mercado Pago API", e);
	    }
	    return null;
	}
	

}
