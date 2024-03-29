package com.juke.api.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import com.juke.api.service.NotificationService;
import com.juke.api.service.TrackQueueService;
import com.juke.api.service.TransactionService;
import com.juke.api.utils.SystemLogger;

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

	/**
	 * THIS ENDPOINT EXPECT THE TRACK INFO AND PAYMENT GATEWAY ID F.E. "Mercado Pago" 
	 */
    @PostMapping("/id")
    public ResponseEntity<String> generatePaymentId(@RequestBody Map<String, Object> requestBody) {
        TrackInfoDTO trackInfoDTO = objectMapper.convertValue(requestBody.get("trackInfoDTO"), TrackInfoDTO.class);
        String paymentGateway = (String) requestBody.get("paymentGateway");
        return trackQueueService.generatePaymentId(trackInfoDTO, paymentGateway);
    }

	//This method is called by mercado pago redirection when the payment is ok. The params are passed by url in MercadoPagoPaymentGatewayImpl
	//FIXME If i access directly here? maybe could be saved the order before payment and change status after complete
	@GetMapping("/success")
	public RedirectView paymentSuccess(@RequestParam(name = "payment_id") String paymentId,
			@RequestParam(name = "trackURI") String trackURI, 
			@RequestParam(name = "amount") Double amount,
			@RequestParam(name = "albumCover") String albumCover,
			@RequestParam(name = "artistName") String artistName,
			@RequestParam(name = "trackName") String trackName) throws UnsupportedEncodingException{
		
		String redirectUrl = CLIENT_SUCCESS_URL;
		try {
			trackQueueService.enqueueTrack(trackURI);
			transactionService.saveNewTransactionAndTrackIfNotExists(paymentId, trackURI, amount, albumCover, artistName, trackName);
			notificationService.saveAndSentToWebSocket(new Notification("Se agregó la canción " + artistName + " - " + trackName, new Timestamp(System.currentTimeMillis())));
		} catch (Exception e) {
			String encodedErrorMessage = URLEncoder.encode("El pago se realizó correctamente, pero ocurrió un error al enviar la canción a la cola. Por favor, comuníqueselo al dueño del establecimiento. Número de pago: " + paymentId, "UTF-8");
	        redirectUrl = CLIENT_FAILED_URL + "?message=" + encodedErrorMessage;
			SystemLogger.error(e.getMessage(), e);
		}

		return new RedirectView(redirectUrl);
	}
	

}
