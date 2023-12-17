package com.juke.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juke.api.model.AdminConfiguration;

@Repository
public interface IAdminConfigurationRepository extends JpaRepository<AdminConfiguration, Long>{
	
	AdminConfiguration findByTypeAndActiveTrue(String type);

}
