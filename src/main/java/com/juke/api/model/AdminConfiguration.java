package com.juke.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "AdminConfiguration")
public class AdminConfiguration extends PersistentObject {
	
	private Boolean isAvailable;
	private BigDecimal trackPrice;
	@Column(unique = true)
	private String type;
	//add from to hours
	
	
	public AdminConfiguration() {
		
	}
	
	public AdminConfiguration(Boolean isAvailable, BigDecimal trackPrice) {
		this.isAvailable = isAvailable;
		this.trackPrice = trackPrice;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public BigDecimal getTrackPrice() {
		return trackPrice;
	}

	public void setTrackPrice(BigDecimal trackPrice) {
		this.trackPrice = trackPrice;
	}
	
	public String getType () {
		return this.type;
	}
	
	public void setType (String type) {
		this.type = type;
	}
	
	
}
