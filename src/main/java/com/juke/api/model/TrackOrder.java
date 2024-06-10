package com.juke.api.model;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.juke.api.utils.OrderState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TrackOrder")
public class TrackOrder extends PersistentObject {

	@Column(unique = true)
	private String externalReference;
	
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	private OrderState state;
	private Timestamp creationTimestamp;
	
	@ManyToOne
	@JoinColumn(name = "track_id")
	private Track track;
	
	public TrackOrder() {
		
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public Timestamp getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Timestamp creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}
	

}
