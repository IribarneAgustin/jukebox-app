package com.juke.api.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Transaction")
public class Transaction extends PersistentObject {

	private Timestamp creationTimestamp;
	private BigDecimal amount;

	@Column(unique = true)
	private String paymentId;

	@ManyToOne
	@JoinColumn(name = "track_id")
	private Track track;
	
	@OneToOne
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private TrackOrder trackOrder;
	

	public Transaction() {
		super();
	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	public Timestamp getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Timestamp creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public TrackOrder getTrackOrder() {
		return trackOrder;
	}

	public void setTrackOrder(TrackOrder trackOrder) {
		this.trackOrder = trackOrder;
	}
	
	

}
