package com.juke.api.model;

import java.time.LocalTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("APP_CONFIG")
public class AppConfiguration extends AdminConfiguration {

	private String spotifyPlaylistId;
	private LocalTime fromHour;
	private LocalTime toHour;
	private Boolean isAvailable;
	//payment gateway?

	public AppConfiguration() {
		super();
	}

	public AppConfiguration(String spotifyPlaylistId, LocalTime fromHour, LocalTime toHour, Boolean isAvailable) {
		super();
		this.spotifyPlaylistId = spotifyPlaylistId;
		this.fromHour = fromHour;
		this.toHour = toHour;
		this.isAvailable = isAvailable;
	}

	public String getSpotifyPlaylistId() {
		return spotifyPlaylistId;
	}

	public void setSpotifyPlaylistId(String spotifyPlaylistId) {
		this.spotifyPlaylistId = spotifyPlaylistId;
	}

	public LocalTime getFromHour() {
		return fromHour;
	}

	public void setFromHour(LocalTime fromHour) {
		this.fromHour = fromHour;
	}

	public LocalTime getToHour() {
		return toHour;
	}

	public void setToHour(LocalTime toHour) {
		this.toHour = toHour;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

}
