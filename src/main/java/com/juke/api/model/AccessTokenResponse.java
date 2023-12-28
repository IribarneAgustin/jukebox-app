package com.juke.api.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "AccessTokenResponse")
public class AccessTokenResponse extends PersistentObject{

	@Column(length = 512)
	private String accessToken;
	@Column(length = 512)
	private String refreshToken;
	private Timestamp expirationTime;
	@Column(unique = true)
	private String serviceId; //Constants

    public AccessTokenResponse(String accessToken, String refreshToken, Timestamp expirationTime, String serviceId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
        this.serviceId = serviceId;
        super.setActive(Boolean.TRUE);
    }

	public AccessTokenResponse() {
	}
	
	

	public Timestamp getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Timestamp expirationTime) {
		this.expirationTime = expirationTime;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	
}
