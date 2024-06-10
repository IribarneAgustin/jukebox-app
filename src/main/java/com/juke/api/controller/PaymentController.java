package com.juke.api.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juke.api.dto.TrackInfoDTO;
import com.juke.api.model.Notification;
import com.juke.api.model.TrackOrder;
import com.juke.api.service.MercadoPagoService;
import com.juke.api.service.NotificationService;
import com.juke.api.service.OrderService;
import com.juke.api.service.TrackQueueService;
import com.juke.api.service.TransactionService;
import com.juke.api.service.WebhookService;
import com.juke.api.utils.OrderState;
import com.juke.api.utils.SystemLogger;
import com.juke.api.utils.TrackEnqueueException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private TrackQueueService trackQueueService;

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private NotificationService notificationService;

	@Value("${CLIENT_SUCCESS_URL}")
	private String CLIENT_SUCCESS_URL;
	
	@Value("${CLIENT_FAILED_URL}")
	private String CLIENT_FAILED_URL;
	
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private WebhookService webhookService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private MercadoPagoService mercadoPagoService;

	/**
	 * THIS ENDPOINT EXPECT THE TRACK INFO AND PAYMENT GATEWAY ID F.E. "Mercado Pago" 
	 */
    @PostMapping("/id")
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<String> generatePaymentId(@RequestBody Map<String, Object> requestBody) {
        TrackInfoDTO trackInfoDTO = objectMapper.convertValue(requestBody.get("trackInfoDTO"), TrackInfoDTO.class);
        String paymentGateway = (String) requestBody.get("paymentGateway");
        return trackQueueService.generatePaymentId(trackInfoDTO, paymentGateway);
    }

	/*
	 * This method is called by mercado pago redirection when the payment is ok
	 */
    @GetMapping("/success")
	public RedirectView paymentSuccess(@RequestParam String orderId) throws UnsupportedEncodingException {
		String redirectUrl = CLIENT_SUCCESS_URL;
		Boolean success = false;

		try {
			success = orderService.verifyOrderStateWithAttempts(Long.valueOf(orderId), OrderState.SUCCESS);
			if (!success) {
				redirectUrl = CLIENT_FAILED_URL + "?message=" + URLEncoder.encode("No se pudo agregar la canción, por favor vuelva a intentarlo", "UTF-8");;
			}

		} catch (Exception e) {
            SystemLogger.error("Error al redireccionar el usuario despues del pago", e);
			redirectUrl = CLIENT_FAILED_URL + "?message=" + URLEncoder.encode("No se pudo agregar la canción, por favor vuelva a intentarlo", "UTF-8");;
		}

		return new RedirectView(redirectUrl);
	}

	
	
	/*
	 * ASYNC METHOD
	 * MERCADO PAGO EXPECTS HTTP RESPONSE 200 OR 201 TO BE NOTIFIED THAT NOTIFICATION WAS RECEIVED, IF NOT WILL TRY SENDING NOTIFICATION AGAIN.   
	 */
	@PostMapping("/webhook")
	public ResponseEntity<String> handleWebhook(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
		ResponseEntity<String> response = null;
		String paymentId = null;
		TrackOrder order = new TrackOrder();
		try {
			Boolean isValid = webhookService.validateSignature(request);
			if (isValid) {
				paymentId = (String) ((Map<String, Object>) payload.get("data")).get("id");
				Map<String, Object> paymentData = webhookService.getPaymentIfApproved(paymentId);
				String externalReference = (String) paymentData.get("external_reference");
				order = orderService.findByExternalReference(externalReference);

				// This condition determines that payment was processed successfully
				if (paymentData != null && order != null && !order.getState().equals(OrderState.SUCCESS)) {
					// Track enqueue
					trackQueueService.enqueueTrack(order.getTrack().getSpotifyURI());

					// Set new Order status and save Transaction
					order.setState(OrderState.SUCCESS);
					orderService.save(order);
					transactionService.saveNewTransaction(order);

					// Notify to websocket
					notificationService.saveAndSentToWebSocket(new Notification("Se agregó la canción " + order.getTrack().getArtistName() + " - " + order.getTrack().getTrackName(), new Timestamp(System.currentTimeMillis())));
					response = new ResponseEntity<>("Payment successful", HttpStatus.OK);
				} else {
					// Failed payment
					response = new ResponseEntity<>("Payment failed", HttpStatus.OK);
				}
			} else {
				response = new ResponseEntity<>("Invalid signature", HttpStatus.OK);
			}
		} catch (TrackEnqueueException e) {
			SystemLogger.error("El pagó se realizó correctamente pero ocurrió un error inesperado al encolar la canción", e);
			mercadoPagoService.refundCash(paymentId, order);
			response = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);	
		}
		catch (Exception e) {
			SystemLogger.error("Error al procesar el webhook de pago", e);
			response = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}
	

	

}
