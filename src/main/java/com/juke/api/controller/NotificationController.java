package com.juke.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.model.Notification;
import com.juke.api.service.NotificationService;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@GetMapping("/get")
	public List<Notification> findFirst10ByOrderByCreationTimestampDesc() {
		return notificationService.findFirst10ByOrderByCreationTimestampDesc();
	}

}
