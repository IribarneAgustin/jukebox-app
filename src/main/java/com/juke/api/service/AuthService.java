package com.juke.api.service;

import org.springframework.beans.factory.annotation.Autowired;
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

	public AuthResponse login(LoginRequest request) throws Exception {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		UserDetails user = adminRepository.findByUsername(request.getUsername());

		if (user == null) {
			throw new Exception("User does not exists");
		}
		AuthResponse response = new AuthResponse();
		response.setToken(jwtService.getToken(user));
		return response;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) throws Exception {
		AuthResponse authResponse = new AuthResponse();
		try {
			Administrator admin = new Administrator();
			admin.setActive(Boolean.TRUE);
			admin.setPassword(passwordEncoder.encode(request.getPassword()));
			admin.setUsername(request.getUsername());

			adminRepository.save(admin);

			authResponse.setToken(jwtService.getToken(admin));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return authResponse;

	}

}
