package com.juke.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juke.api.model.AccessTokenResponse;

@Repository
public interface IAccessTokenResponseRepository extends JpaRepository<AccessTokenResponse, Long>{
	
	Optional<AccessTokenResponse> findByServiceId(String serviceId);

}
