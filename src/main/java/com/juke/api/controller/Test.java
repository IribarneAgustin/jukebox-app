package com.juke.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.model.Track;
import com.juke.api.service.SpotifyWebApiService;
import com.juke.api.service.TrackService;


@RestController
@RequestMapping("/api")
public class Test {
	
	@Autowired
	private SpotifyWebApiService spotifyService;
	
	@Autowired
	private TrackService trackService;
	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("test");
	}
    private final SimpMessagingTemplate messagingTemplate;

    public Test(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/notification")
    public void handleNotification(String notificationText) {
        // Process the notificationText as needed
        // For example, you can log it or send it to other clients
        String processedNotification = "Processed Notification: " + notificationText;

        // Send the processed notification to all subscribers
        messagingTemplate.convertAndSend("/topic/notifications", processedNotification);
    }
    
    @GetMapping("/tracks")
    private void testTracks() {
    	trackService.storeTracksFromPlaylists();
    }
    
    @GetMapping("/find")
    private List<Track> testFindTracks(String input) {
    	return trackService.searchTracksByUserInput(input);
    }


}
