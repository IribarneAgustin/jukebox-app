package com.juke.api.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.juke.api.model.Notification;
import com.juke.api.repository.INotificationRepository;
import com.juke.api.utils.SystemLogger;

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
			SystemLogger.info("Notification sent successfully");
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public void sendNotification(Notification notification) {
	    messagingTemplate.convertAndSend("/topic/notifications", notification);
	}

	public List<Notification> findFirst5ByOrderByCreationTimestampDesc() {
		return notificationRepository.findFirst5ByOrderByCreationTimestampDesc();
	}
	
	public ResponseEntity<List<Notification>> findFirst5Notifications() {
	    ResponseEntity<List<Notification>> responseEntity = null;

	    try {
	        List<Notification> notificationList = findFirst5ByOrderByCreationTimestampDesc();
	        HttpStatus status = notificationList != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
	        responseEntity = new ResponseEntity<>(notificationList, status);
	    } catch (Exception e) {
	        SystemLogger.error(e.getMessage(), e);
	        responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    return responseEntity;
	}
	
	

}
