package com.juke.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class Test {
	
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


}
