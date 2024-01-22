package com.juke.api.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.juke.api.model.Notification;
import com.juke.api.repository.INotificationRepository;

@Service
public class NotificationService {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private INotificationRepository notificationRepository;

	public void saveAndSentToWebSocket(Notification notification) {
		try {
			notificationRepository.save(notification);
			sendNotification(notification);
			System.out.println("Notification sent successfully " + new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void sendNotification(Notification notification) {
	    messagingTemplate.convertAndSend("/topic/notifications", notification);
	}

	public List<Notification> findFirst5ByOrderByCreationTimestampDesc() {
		return notificationRepository.findFirst5ByOrderByCreationTimestampDesc();
	}

}
