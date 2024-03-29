package com.juke.api.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.juke.api.dto.AuthResponse;
import com.juke.api.dto.LoginRequest;
import com.juke.api.dto.RegisterRequest;
import com.juke.api.model.Administrator;
import com.juke.api.repository.IAdministratorRepository;
import com.juke.api.security.JwtService;
import com.juke.api.utils.SystemLogger;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private IAdministratorRepository adminRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${ADMIN_USERNAME}") 
	private String adminUsername;
	
	@Value("${ADMIN_PASSWORD}") 
	private String adminPassword;

	public AuthResponse login(LoginRequest request) throws Exception {
		AuthResponse response = new AuthResponse();
		try {
			UserDetails user = adminRepository.findByUsername(request.getUsername());

			if (user == null) {
				throw new Exception("Bad credentials");
			}

			authManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			response.setToken(jwtService.getToken(user));
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
		return response;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) throws Exception {
		AuthResponse authResponse = new AuthResponse();
		try {
			if (adminRepository.findByUsername(request.getUsername()) == null) {
				Administrator admin = new Administrator();
				admin.setActive(Boolean.TRUE);
				admin.setPassword(passwordEncoder.encode(request.getPassword()));
				admin.setUsername(request.getUsername());

				adminRepository.save(admin);

				authResponse.setToken(jwtService.getToken(admin));
			} else {
				throw new Exception("Admin already exists");
			}
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}
		return authResponse;

	}

	@PostConstruct
	public void createAdminIfNotExists() {
		try {
			if (adminRepository.findByUsername(adminUsername) == null) {
				Administrator admin = new Administrator();
				admin.setActive(Boolean.TRUE);
				admin.setPassword(passwordEncoder.encode(adminPassword));
				admin.setUsername(adminUsername);
				adminRepository.save(admin);
			}
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}

	}

}
