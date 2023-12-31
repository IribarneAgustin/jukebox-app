package com.juke.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.service.SpotifyWebApiService;
import com.juke.api.service.TransactionService;
import com.juke.api.dto.TrackInfoDTO;
@RestController
@RequestMapping("/spotify")
public class MusicSearchController {
	
	@Autowired
	private SpotifyWebApiService spotifyWebApiService;
	
	@Autowired
	private TransactionService transactionService;
	
	@GetMapping("/artist/{name}")
	public ResponseEntity<String> getArtistInformationByName(@PathVariable(name = "name") String artistName) {
	    return spotifyWebApiService.getArtistInformationByName(artistName);
	}
	
	@GetMapping("/track/{name}")
	public ResponseEntity<String> getTrackInformationByName(@PathVariable(name = "name") String artistName) {
		return spotifyWebApiService.getTrackInformationByName(artistName);

	}
	
	@GetMapping("/track/list/queue")
	public List<TrackInfoDTO> getTrackQueue () {
		return transactionService.getTrackQueue();
		
	}

	

}
