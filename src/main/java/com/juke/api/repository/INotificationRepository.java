package com.juke.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juke.api.model.Notification;

public interface INotificationRepository extends JpaRepository<Notification, Long>{

	List<Notification> findFirst10ByOrderByCreationTimestampDesc();

}
