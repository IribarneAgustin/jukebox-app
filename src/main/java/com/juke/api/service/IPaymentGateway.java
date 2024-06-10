package com.juke.api.service;


import org.springframework.http.HttpStatusCode;

import com.juke.api.dto.PaymentDTO;
import com.juke.api.model.TrackOrder;

public interface IPaymentGateway {
	
	public void process(PaymentDTO paymentDTO) throws Exception;
	
	public String generatePaymentId(PaymentDTO paymentDTO, TrackOrder order) throws Exception;
	
	public HttpStatusCode refundCash(String paymentId, String token, TrackOrder order) throws Exception;

}
