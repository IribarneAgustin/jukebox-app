package com.juke.api.dto;

import java.sql.Timestamp;

public class TrackInfoDTO {

	private String trackUri;
	private String trackName;
	private String artistName;
	private String albumCover;
	private Timestamp addedAt;

	public TrackInfoDTO() {

	}

	public TrackInfoDTO(String trackUri, String trackName, String artistName, String albumCover, Timestamp addedAt) {
		super();
		this.trackUri = trackUri;
		this.trackName = trackName;
		this.artistName = artistName;
		this.albumCover = albumCover;
		this.addedAt = addedAt;
	}

	public Timestamp getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(Timestamp addedAt) {
		this.addedAt = addedAt;
	}

	public String getAlbumCover() {
		return albumCover;
	}

	public void setAlbumCover(String albumCover) {
		this.albumCover = albumCover;
	}

	public String getTrackUri() {
		return trackUri;
	}

	public void setTrackUri(String trackUri) {
		this.trackUri = trackUri;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

}
