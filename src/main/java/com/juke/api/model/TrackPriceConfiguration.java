package com.juke.api.model;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PRICE_CONFIG")
public class TrackPriceConfiguration extends AdminConfiguration {

	private BigDecimal trackPrice;
	private String currency;

	public TrackPriceConfiguration() {
		super();
	}

	public TrackPriceConfiguration(BigDecimal trackPrice, String currency) {
		super();
		this.trackPrice = trackPrice;
		this.currency = currency;
	}

	public BigDecimal getTrackPrice() {
		return trackPrice;
	}

	public void setTrackPrice(BigDecimal trackPrice) {
		this.trackPrice = trackPrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
