package com.juke.api.dto;

public class TrackInfoDTO {

	private String trackUri;
	private String trackName;
	private String artistName;

	public TrackInfoDTO() {

	}

	public TrackInfoDTO(String trackUri, String trackName, String artistName) {
		this.trackUri = trackUri;
		this.trackName = trackName;
		this.artistName = artistName;
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
