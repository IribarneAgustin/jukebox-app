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
	private String spotifyURI;
	private String description;

	public Track(String albumCover, String artistName, String trackName, String spotifyURI,String description) {
		super();
		this.albumCover = albumCover;
		this.artistName = artistName;
		this.trackName = trackName;
		this.spotifyURI = spotifyURI;
		this.setDescription(description);
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

	public String getSpotifyURI() {
		return spotifyURI;
	}

	public void setSpotifyURI(String spotifyID) {
		this.spotifyURI = spotifyID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
