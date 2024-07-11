package com.juke.api.controller;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Map;

import com.juke.api.service.TrackService;
import com.juke.api.utils.SystemLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.service.AdminConfigurationService;

@RestController
@RequestMapping("/api/admin")
public class AdminConfigurationController {
	
	@Autowired
	private AdminConfigurationService adminConfigService;
	
	@PostMapping("/track/price")
	public ResponseEntity<String> setTrackPrice(@RequestBody Map<String, BigDecimal> requestBody) {
	    BigDecimal trackPrice = requestBody.get("trackPrice");
	    return adminConfigService.setTrackPrice(trackPrice);
	}
	
	@GetMapping("/track/get/price")
	public ResponseEntity<Map<String, Object>> getTrackPrice() {
	    return adminConfigService.getTrackPrice();
	}
	
	@PostMapping("/app/playlist/id")
	public ResponseEntity<String> setPlaylistId(@RequestBody Map<String, String> request) {
		String playlistId = request.get("playlistId");
		return adminConfigService.setPlaylistId(playlistId);
	}
	
	@GetMapping("/app/get/playlist/id")
	public ResponseEntity<Map<String, Object>> getPlaylistId() {
		return adminConfigService.getPlaylistId();
	}
	
	@PostMapping("/app/schedule")
	public ResponseEntity<String> setSchedule(@RequestBody Map<String, String> request) {
	    String fromHourStr = request.get("fromHour");
	    String toHourStr = request.get("toHour");
	    return adminConfigService.setSchedule(fromHourStr, toHourStr);
	}
	
	@GetMapping("/app/get/schedule")
	public ResponseEntity<Map<String, LocalTime>> getSchedule() {
		return adminConfigService.getSchedule();
	}
	
	@PostMapping("/app/set/status")
	public ResponseEntity<String> setApplicationStatus(@RequestBody Map<String, Boolean> request) {
		return adminConfigService.setApplicationStatus(request.get("status"));
	}
	
	@GetMapping("/app/get/status")
	public ResponseEntity<Map<String, Object>> getStatus() {
		return adminConfigService.getStatus();
	}


}
