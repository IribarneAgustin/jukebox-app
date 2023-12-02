package com.juke.api.service;

public class PaymentContext {

	private IPaymentGateway paymentGateway;


	public void setPaymentGateway(String paymentGatewayName) {
		this.paymentGateway = createPaymentGateway(paymentGatewayName);
	}

	public void processPayment(double amount) {
		if (paymentGateway == null) {
			throw new IllegalStateException("Payment gateway not set.");
		}
		System.out.println("Starting payment process...");
		paymentGateway.process(amount);
		System.out.println("Payment process completed.");
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
