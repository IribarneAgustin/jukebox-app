package com.juke.api.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	
	@PostMapping("/trackPrice")
	public ResponseEntity<String> setTrackPrice(@RequestBody Map<String, BigDecimal> requestBody) {
	    BigDecimal trackPrice = requestBody.get("trackPrice");
	    return adminConfigService.setTrackPrice(trackPrice);
	}

	

}
