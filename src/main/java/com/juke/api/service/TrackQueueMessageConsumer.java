package com.juke.api.service;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.juke.api.controller.SpotifyController;

@Component
public class TrackQueueMessageConsumer {
	
	
	@Autowired
	private SpotifyPlaybackSDK spotifyPlaybackSKDService;

	@Autowired 
	SpotifyController spotifyController;
	
    @JmsListener(destination = "tracks")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
        // check admin config to know if auto play is enabled
        System.out.println("Received message: " + spotifyController.accessToken);
    	spotifyPlaybackSKDService.playSong(message,spotifyController.accessToken);
     
    }

}
