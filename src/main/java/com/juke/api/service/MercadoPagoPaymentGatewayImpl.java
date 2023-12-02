package com.juke.api.service;

public class MercadoPagoPaymentGatewayImpl implements IPaymentGateway{

	@Override
	public void process(Double amount) {
		System.out.println("Processing Mercado Pago payment: $" + amount);
	}

}
