package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.service.SpotifyWebApiService;

@RestController
@RequestMapping("/api")
public class MusicSearchController {
	
	@Autowired
	private SpotifyWebApiService spotifyWebApiService;
	
	@GetMapping("/artist/{name}")
	public ResponseEntity<String> getArtistInformationByName(@PathVariable(name = "name") String artistName) {
	    return spotifyWebApiService.getArtistInformationByName(artistName);
	}
	
	@GetMapping("/track/{name}")
	public ResponseEntity<String> getTrackInformationByName(@PathVariable(name = "name") String artistName) {
	    return spotifyWebApiService.getTrackInformationByName(artistName);
	}

	

}
