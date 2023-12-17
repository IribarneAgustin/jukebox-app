package com.juke.api.service;

import com.juke.api.dto.PaymentDTO;

public class PaymentContext {

	private IPaymentGateway paymentGateway;


	public void setPaymentGateway(String paymentGatewayName) {
		this.paymentGateway = createPaymentGateway(paymentGatewayName);
	}

	public void processPayment(PaymentDTO paymentDTO) throws Exception{
		if (paymentGateway == null) {
			throw new IllegalStateException("Payment gateway not set.");
		}
		System.out.println("Starting payment process...");
		try {
			paymentGateway.process(paymentDTO); // handled in the client
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println("Payment process completed.");
	}
	
	public String generatePaymentId(PaymentDTO paymentDTO) throws Exception{
		String redirectUrl = null;
		if (paymentGateway == null) {
			throw new IllegalStateException("Payment gateway not set.");
		}
		System.out.println("Generating payment id");
		try {
			redirectUrl = paymentGateway.generatePaymentId(paymentDTO);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println("Payment id generated succesfully");
		
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
