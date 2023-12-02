package com.juke.api.model;

import java.math.BigDecimal;


public class AdminConfiguration {
	
	private Boolean isAvailable;
	private BigDecimal trackPrice;
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
	
	
	
	

}
