package com.juke.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Track")
public class Track extends PersistentObject{

	private String albumCover;
	private String artistName;
	private String trackName;
	@Column(unique = true)
	private String spotifyId;

	public Track(String albumCover, String artistName, String trackName, String spotifyID) {
		super();
		this.albumCover = albumCover;
		this.artistName = artistName;
		this.trackName = trackName;
		this.spotifyId = spotifyID;
	}

	public Track() {
		super();
	}

	public String getAlbumCover() {
		return albumCover;
	}

	public void setAlbumCover(String albumCover) {
		this.albumCover = albumCover;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyID) {
		this.spotifyId = spotifyID;
	}

}
