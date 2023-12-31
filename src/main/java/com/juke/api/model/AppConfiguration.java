package com.juke.api.model;

import java.sql.Timestamp;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("APP_CONFIG")
public class AppConfiguration extends AdminConfiguration {

	private String spotifyPlaylistId;
	private Timestamp fromHour;
	private Timestamp toHour;
	private Boolean isAvailable;

	public AppConfiguration() {
		super();
	}

	public AppConfiguration(String spotifyPlaylistId, Timestamp fromHour, Timestamp toHour, Boolean isAvailable) {
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

	public Timestamp getFromHour() {
		return fromHour;
	}

	public void setFromHour(Timestamp fromHour) {
		this.fromHour = fromHour;
	}

	public Timestamp getToHour() {
		return toHour;
	}

	public void setToHour(Timestamp toHour) {
		this.toHour = toHour;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

}
