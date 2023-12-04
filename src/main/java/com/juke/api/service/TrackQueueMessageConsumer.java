package com.juke.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.juke.api.model.AccessTokenResponse;

@Component
public class TrackQueueMessageConsumer {
	
	
	@Autowired
	private SpotifyPlaybackSDK spotifyPlaybackSKDService;

	@Autowired
	private SpotifyAuthService authService;
	
    @JmsListener(destination = "tracks")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
        try {
			spotifyPlaybackSKDService.addTracksToPlaylist(message,authService.getToken());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
    }

}
