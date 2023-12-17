package com.juke.api.dto;

public class PaymentDTO {

	private String description;
	private Double price;
	private Integer quantity;
	private String currency;
	private String token;
	private String successUrl;
	private String failedUrl;
	private TrackInfoDTO trackInfoDTO;

	public PaymentDTO() {

	}

	public PaymentDTO(String description, Double price, Integer quantity, String currency, String token,
			String successUrl, String failedUrl, TrackInfoDTO trackInfoDTO) {
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.currency = currency;
		this.token = token;
		this.successUrl = successUrl;
		this.failedUrl = failedUrl;
		this.trackInfoDTO = trackInfoDTO;
	}

	public TrackInfoDTO getTrackInfoDTO() {
		return trackInfoDTO;
	}

	public void setTrackInfoDTO(TrackInfoDTO trackInfoDTO) {
		this.trackInfoDTO = trackInfoDTO;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getFailedUrl() {
		return failedUrl;
	}

	public void setFailedUrl(String failedUrl) {
		this.failedUrl = failedUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
