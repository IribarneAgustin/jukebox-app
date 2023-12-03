package com.juke.api.service;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.juke.api.controller.SpotifyController;
import com.juke.api.model.AccessTokenResponse;

@Component
public class TrackQueueMessageConsumer {
	
	
	@Autowired
	private SpotifyPlaybackSDK spotifyPlaybackSKDService;

	@Autowired 
	SpotifyController spotifyController;
	
	@Autowired
	private SpotifyAuthService authService;
	
    @JmsListener(destination = "tracks")
    public void receiveMessage(String message) {
    	AccessTokenResponse newToken = new AccessTokenResponse();
        System.out.println("Received message: " + message);
        // check admin config to know if auto play is enabled
        System.out.println("token " + spotifyController.accessToken);
        System.out.println("refresh token " + spotifyController.refreshToken);
        try {
			newToken = authService.refreshAccessToken(spotifyController.refreshToken);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("new token " + newToken.getAccessToken());
    	//spotifyPlaybackSKDService.playSong(message,newToken.getAccessToken());
        spotifyPlaybackSKDService.addTracksToPlaylist(message,newToken.getAccessToken());
     
    }

}
