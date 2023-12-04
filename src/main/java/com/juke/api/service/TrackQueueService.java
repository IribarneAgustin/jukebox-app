package com.juke.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juke.api.dto.TrackInfoDTO;

@Service
public class TrackQueueService {
	
	
	private PaymentContext paymentContext = new PaymentContext();
	
	@Autowired
	private TrackQueueMessageProducer queueProducer;
	
	
	public ResponseEntity<String> enqueueTrack(TrackInfoDTO trackInfoDTO, String paymentGateway) {
		
        // check admin config to know if service is enabled
		paymentContext.setPaymentGateway(paymentGateway);
		paymentContext.processPayment(0); //get amount from AdminConfiguration
		queueProducer.sendMessage("tracks",trackInfoDTO.getTrackUri());
		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
