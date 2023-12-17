package com.juke.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import com.juke.api.dto.PaymentDTO;

public class MercadoPagoPaymentGatewayImpl implements IPaymentGateway {

	@Override
	public void process(PaymentDTO paymentDTO) throws Exception {
		//handled in the client		
	}

	@Override
	public String generatePaymentId(PaymentDTO paymentDTO) throws Exception {//add try catch
		
		Map<String, Object> preference = new HashMap<>();
				
        preference.put("items", List.of(
                Map.of(
                        "title", paymentDTO.getDescription(),
                        "unit_price", Double.parseDouble(paymentDTO.getPrice().toString()),
                        "quantity", paymentDTO.getQuantity()//,
                        //"currency_id", paymentDTO.getCurrency() Si se omite, toma la config de tu cuenta de MP
                )
        ));
        preference.put("back_urls", Map.of(
                "success", paymentDTO.getSuccessUrl() + "?trackURI=" + paymentDTO.getTrackInfoDTO().getTrackUri() + "&amount=" + paymentDTO.getPrice(),
                "failure", paymentDTO.getFailedUrl(),
                "pending", ""
        ));
        preference.put("auto_return", "approved");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.mercadopago.com/checkout/preferences?access_token=" + paymentDTO.getToken(),
                preference,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && response.getStatusCode() == HttpStatus.CREATED) {
            return (String) responseBody.get("id");
        } else {
        	throw new Exception(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
	
	}
	

}
