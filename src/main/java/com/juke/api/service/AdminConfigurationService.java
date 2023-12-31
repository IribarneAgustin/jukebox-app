package com.juke.api.service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	
	public ResponseEntity<Map<String, Object>> getTrackPrice() {
	    try {
	        TrackPriceConfiguration trackPriceConfig = adminConfigRepo.findTrackPriceConfigurationByActiveTrue();
	        if (trackPriceConfig != null) {
	            BigDecimal price = trackPriceConfig.getTrackPrice();

	            Map<String, Object> responseBody = new HashMap<>();
	            responseBody.put("trackPrice", price);

	            return ResponseEntity.ok(responseBody);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Configuration not found"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
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
	
	public ResponseEntity<Map<String, Object>> getPlaylistId() {
	    try {
	        AppConfiguration appConfig = adminConfigRepo.findAppConfigurationByActiveTrue();

	        if (appConfig != null) {
	            String playlistId = appConfig.getSpotifyPlaylistId();

	            Map<String, Object> responseBody = new HashMap<>();
	            responseBody.put("playlistId", playlistId);

	            return ResponseEntity.ok(responseBody);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Configuration not found"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Internal server error"));
	    }
	}


	public ResponseEntity<String> setSchedule(String fromHourStr, String toHourStr) {
		ResponseEntity<String> response = null;
		try {
			
	        LocalTime fromHour = LocalTime.parse(fromHourStr);
	        LocalTime toHour = LocalTime.parse(toHourStr);

			AppConfiguration appConfig = adminConfigRepo.findAppConfigurationByActiveTrue();
			if (appConfig == null) {
				appConfig = new AppConfiguration();
				appConfig.setActive(Boolean.TRUE);
			}
			appConfig.setFromHour(fromHour);
			appConfig.setToHour(toHour);
			adminConfigRepo.save(appConfig);
			response = new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}

		return response;
	}
	
	public ResponseEntity<Map<String, LocalTime>> getSchedule() {
		ResponseEntity<Map<String, LocalTime>> response = null;
		try {
			AppConfiguration appConfig = adminConfigRepo.findAppConfigurationByActiveTrue();
			if (appConfig != null) {
				LocalTime fromHour = appConfig.getFromHour();
				LocalTime toHour = appConfig.getToHour();

				Map<String, LocalTime> hoursMap = new HashMap<>();
				hoursMap.put("fromHour", fromHour);
				hoursMap.put("toHour", toHour);

				response = ResponseEntity.ok().body(hoursMap);
			} else {
				throw new Exception("The schedule does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	public ResponseEntity<String> setApplicationStatus(Boolean status) {
		ResponseEntity<String> response = null;
		try {
			AppConfiguration appConfig = adminConfigRepo.findAppConfigurationByActiveTrue();
			if (appConfig == null) {
				appConfig = new AppConfiguration();
				appConfig.setActive(Boolean.TRUE);
			}
			appConfig.setIsAvailable(status);
			adminConfigRepo.save(appConfig);
			response = new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}

		return response;
	}

	public ResponseEntity<Map<String, Object>> getStatus() {
	    try {
	        AppConfiguration appConfig = adminConfigRepo.findAppConfigurationByActiveTrue();
	        if (appConfig != null) {
	            Boolean status = appConfig.getIsAvailable();
	            
	            Map<String, Object> responseBody = new HashMap<>();
	            responseBody.put("status", status);
	            
	            return ResponseEntity.ok(responseBody);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Configuration not found"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	

}
