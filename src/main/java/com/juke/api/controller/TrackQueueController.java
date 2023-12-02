package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.dto.TrackInfoDTO;
import com.juke.api.service.TrackQueueService;

@RestController
@RequestMapping("/api/track")
public class TrackQueueController {
	
	@Autowired
	private TrackQueueService trackQueueService;
	
	@PostMapping("/enqueue")
	public ResponseEntity<String> enqueueTrack(@RequestBody TrackInfoDTO trackInfoDTO, @RequestParam String paymentGateway) {
	    return trackQueueService.enqueueTrack(trackInfoDTO, paymentGateway);
	}

	
	

}
