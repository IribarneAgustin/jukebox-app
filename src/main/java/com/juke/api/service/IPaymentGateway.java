package com.juke.api.service;


import com.juke.api.dto.PaymentDTO;

public interface IPaymentGateway {
	
	public void process(PaymentDTO paymentDTO) throws Exception;
	
	public String generatePaymentId(PaymentDTO paymentDTO) throws Exception;

}
