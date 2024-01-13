package com.juke.api.model;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Notification")
public class Notification extends PersistentObject {

	private String description;
	private Timestamp creationTimestamp;

	public Notification() {
		super();
	}

	public Notification(String description, Timestamp creationTimestamp) {
		super();
		this.description = description;
		this.creationTimestamp = creationTimestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Timestamp creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

}
