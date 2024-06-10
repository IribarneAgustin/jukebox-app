package com.juke.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.juke.api.model.TrackOrder;
import com.juke.api.utils.SystemLogger;

@Service
public class MercadoPagoService {

	@Autowired
	private MercadoPagoAuthService mercadoPagoAuthService;

	private IPaymentGateway paymentGateway = new MercadoPagoPaymentGatewayImpl();

	public void refundCash(String paymentId, TrackOrder order) {
		try {
			if (paymentId != null) {
				SystemLogger.info("Se procede a cancelar la compra de " + order.getTrack().getDescription() + " externalReference: " + order.getExternalReference());
				HttpStatusCode result = paymentGateway.refundCash(paymentId, mercadoPagoAuthService.getToken(), order);
				if (result.is2xxSuccessful()) {
					SystemLogger.info("Se devolvió el dinero para el paymentId: por un error inesperado al encolar la canción");
				} else {
					throw new Exception("No se pudo devolver el el dinero para el paymentId:" + paymentId);
				}
			}
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
		}
	}

}
