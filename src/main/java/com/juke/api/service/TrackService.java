package com.juke.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.juke.api.model.Track;
import com.juke.api.repository.ITrackRepository;

@Service
public class TrackService {

	@Autowired
	private ITrackRepository trackRepository;
	

	public Track save(Track track) {
		return trackRepository.save(track);
	}

	public Track findBySpotifyId(String spotifyId) {
		return trackRepository.findBySpotifyId(spotifyId);
	}
	

}
