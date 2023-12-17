package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.juke.api.dto.TrackInfoDTO;
import com.juke.api.service.TrackQueueService;
import com.juke.api.service.TransactionService;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080", "http://localhost:5173", "*" })
public class PaymentController {

	@Autowired
	private TrackQueueService trackQueueService;

	@Autowired
	private TransactionService transactionService;
	
	@Value("${CLIENT_SUCCESS_URL}")
	private String CLIENT_SUCCESS_URL;
	
	@Value("${CLIENT_FAILED_URL}")
	private String CLIENT_FAILED_URL;
	

	@PostMapping("/generatePaymentId")
	public ResponseEntity<String> generatePaymentId(@RequestBody TrackInfoDTO trackInfoDTO) {
		return trackQueueService.generatePaymentId(trackInfoDTO, "Mercado Pago");
	}

	//FIXME If i access directly here? 
	//TODO Save Order before this callback
	@GetMapping("/success")
	public RedirectView paymentSuccess(@RequestParam(name = "payment_id") String paymentId, @RequestParam(name = "trackURI") String trackURI, @RequestParam(name = "amount") Double amount) {
		String redirectUrl = CLIENT_SUCCESS_URL;
		try {
			transactionService.saveNewTransaction(paymentId, trackURI, amount);
			trackQueueService.enqueueTrack(trackURI);
		} catch (Exception e) {
			redirectUrl = CLIENT_FAILED_URL + "?messageError=" + 
		            "El pago se realizó correctamente, pero ocurrió un error al enviar la canción a la cola. Por favor, comuníqueselo al dueño del establecimiento. Número de pago: " + paymentId;
		    
			e.printStackTrace();
		}
		
		return new RedirectView(redirectUrl);
	}

}
