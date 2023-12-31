package com.juke.api.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.service.AdminConfigurationService;

@RestController
@RequestMapping("/admin")
public class AdminConfigurationController {
	
	@Autowired
	private AdminConfigurationService adminConfigService;
	
	@PostMapping("/track/price")
	public ResponseEntity<String> setTrackPrice(@RequestBody Map<String, BigDecimal> requestBody) {
	    BigDecimal trackPrice = requestBody.get("trackPrice");
	    return adminConfigService.setTrackPrice(trackPrice);
	}
	
	@GetMapping("/track/get/price")
	public ResponseEntity<String> getTrackPrice() {
	    return adminConfigService.getTrackPrice();
	}
	
	@PostMapping("/app/playlist/id")
	public ResponseEntity<String> setPlaylistId(@RequestBody Map<String, String> request) {
		String playlistId = request.get("playlistId");
		return adminConfigService.setPlaylistId(playlistId);
	}
	
	@GetMapping("/app/get/playlist/id")
	public ResponseEntity<String> getPlaylistId() {
		return adminConfigService.getPlaylistId();
	}
	

}
