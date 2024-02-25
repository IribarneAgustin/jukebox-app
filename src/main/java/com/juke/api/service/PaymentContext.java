package com.juke.api.service;

import com.juke.api.dto.PaymentDTO;
import com.juke.api.utils.SystemLogger;

public class PaymentContext {

	private IPaymentGateway paymentGateway;


	public void setPaymentGateway(String paymentGatewayName) {
		this.paymentGateway = createPaymentGateway(paymentGatewayName);
	}

	public void processPayment(PaymentDTO paymentDTO) throws Exception{
		if (paymentGateway == null) {
			throw new IllegalStateException("Payment gateway not set.");
		}
		SystemLogger.info("Starting payment process...");
		try {
			paymentGateway.process(paymentDTO); // handled in the client
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
		SystemLogger.info("Payment process completed.");
	}
	
	public String generatePaymentId(PaymentDTO paymentDTO) throws Exception{
		String redirectUrl = null;
		if (paymentGateway == null) {
			throw new IllegalStateException("Payment gateway not set.");
		}
		SystemLogger.info("Generating payment id");
		try {
			redirectUrl = paymentGateway.generatePaymentId(paymentDTO);
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
		SystemLogger.info("Payment id generated succesfully");
		
		return redirectUrl;
	}
	

	private IPaymentGateway createPaymentGateway(String paymentGatewayName) {
		switch (paymentGatewayName) {
		case "Mercado Pago":
			return new MercadoPagoPaymentGatewayImpl();
		default:
			throw new IllegalArgumentException("Invalid payment gateway: " + paymentGatewayName);
		}
	}
}
