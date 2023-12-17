package com.juke.api.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Transaction")
public class Transaction extends PersistentObject {

	private Timestamp creationTimestamp;
	private BigDecimal amount;
	
	@Column(unique = true)
	private String paymentId;
	private String trackUri;

	public Transaction() {
		super();
	}

	public Transaction(Timestamp creationTimestamp, BigDecimal amount, String paymentId, String trackUri) {
		super();
		this.creationTimestamp = creationTimestamp;
		this.amount = amount;
		this.paymentId = paymentId;
		this.trackUri = trackUri;
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

	public String getTrackUri() {
		return trackUri;
	}

	public void setTrackUri(String trackUri) {
		this.trackUri = trackUri;
	}

}
