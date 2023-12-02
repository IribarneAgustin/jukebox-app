package com.juke.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
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
        System.out.println(spotifyController.accessToken);
    	spotifyPlaybackSKDService.playSong(message,spotifyController.accessToken);
     
    }

}
