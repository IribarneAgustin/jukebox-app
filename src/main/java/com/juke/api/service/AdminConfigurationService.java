package com.juke.api.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juke.api.model.AdminConfiguration;
import com.juke.api.repository.IAdminConfigurationRepository;
import com.juke.api.utils.AdminConfigurationConstants;

@Service
public class AdminConfigurationService {
	
	@Autowired
	private IAdminConfigurationRepository adminConfigRepo;
	
	
	
	public ResponseEntity<String> setTrackPrice(BigDecimal price){
		ResponseEntity<String> response = null;
		try {
			AdminConfiguration adminConfig = adminConfigRepo.findByTypeAndActiveTrue(AdminConfigurationConstants.ADMIN_CONFIG_TYPE_PRICES); //get by type and must be UNIQUE column
			if(adminConfig == null) {
				adminConfig = createDefaultAdminConfiguration(AdminConfigurationConstants.ADMIN_CONFIG_TYPE_PRICES);
			}
			adminConfig.setTrackPrice(price);
			adminConfigRepo.save(adminConfig);
			response = new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception e) {
			response = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
		
		return response;
	}
	
	private AdminConfiguration createDefaultAdminConfiguration(String type) {
		AdminConfiguration adminConfig = new AdminConfiguration();
		adminConfig.setIsAvailable(Boolean.TRUE);
		adminConfig.setActive(Boolean.TRUE);
		adminConfig.setType(type);
		return adminConfig;
	}
	
	public AdminConfiguration findAdminConfigurationByType(String type) {
		return adminConfigRepo.findByTypeAndActiveTrue(type);
	}

}
