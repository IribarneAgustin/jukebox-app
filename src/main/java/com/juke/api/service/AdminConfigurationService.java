package com.juke.api.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juke.api.model.AdminConfiguration;
import com.juke.api.model.AppConfiguration;
import com.juke.api.model.TrackPriceConfiguration;
import com.juke.api.repository.IAdminConfigurationRepository;

@Service
public class AdminConfigurationService {
	
	@Autowired
	private IAdminConfigurationRepository adminConfigRepo;
	
	
	
	public ResponseEntity<String> setTrackPrice(BigDecimal price) {
		ResponseEntity<String> response = null;
		try {
			
			if (price == null) {
				throw new Exception("Price must not be null");
			}
			
			TrackPriceConfiguration trackPriceConfig = adminConfigRepo.findTrackPriceConfigurationByActiveTrue(); // get by type and must be UNIQUE column
			if (trackPriceConfig == null) {
				trackPriceConfig = new TrackPriceConfiguration();
				trackPriceConfig.setActive(Boolean.TRUE);
			}
			trackPriceConfig.setTrackPrice(price);
			adminConfigRepo.save(trackPriceConfig);
			response = new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}

		return response;
	}
	
	public ResponseEntity<String> getTrackPrice() {
		ResponseEntity<String> response = null;
		try {
			TrackPriceConfiguration trackPriceConfig = adminConfigRepo.findTrackPriceConfigurationByActiveTrue();
			if (trackPriceConfig != null) {
				BigDecimal price = trackPriceConfig.getTrackPrice();
				response = ResponseEntity.ok().body("{\"trackPrice\": " + price + "}");
			} else {
				throw new Exception("The price setting does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	public AdminConfiguration findAdminConfigurationByType(String type) {
		return adminConfigRepo.findByTypeAndActiveTrue(type);
	}

	public AppConfiguration findAppConfigurationByActiveTrue() {
		return adminConfigRepo.findAppConfigurationByActiveTrue();
	}
	
	public TrackPriceConfiguration findTrackPriceConfigurationByActiveTrue() {
		return adminConfigRepo.findTrackPriceConfigurationByActiveTrue();
	}
	
	public ResponseEntity<String> setPlaylistId(String playlistId) {
		ResponseEntity<String> response = null;
		try {

			if (playlistId == null) {
				throw new Exception("Playlist ID must not be null");
			}

			AppConfiguration appConfig = adminConfigRepo.findAppConfigurationByActiveTrue();
			if (appConfig == null) {
				appConfig = new AppConfiguration();
				appConfig.setActive(Boolean.TRUE);
			}
			appConfig.setSpotifyPlaylistId(playlistId);
			adminConfigRepo.save(appConfig);
			response = new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}

		return response;

	}
	
	public ResponseEntity<String> getPlaylistId() {
		ResponseEntity<String> response = null;
		try {
			AppConfiguration appConfig = adminConfigRepo.findAppConfigurationByActiveTrue();
			if (appConfig != null) {
				String playlistId = appConfig.getSpotifyPlaylistId();
				response = ResponseEntity.ok().body("{\"playlistId\": \"" + playlistId + "\"}");

			} else {
				throw new Exception("The playlistId does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	

}
