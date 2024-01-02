package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.dto.AuthResponse;
import com.juke.api.dto.LoginRequest;
import com.juke.api.dto.RegisterRequest;
import com.juke.api.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
	    try {
	        AuthResponse authResponse = authService.login(request);
	        String jwtToken = authResponse.getToken();

	        // Set the cookie with SameSite attribute as "None" for cross-origin requests
	        String cookieHeader = String.format("jwtToken=%s; SameSite=None; Secure; HttpOnly; Max-Age=%d; Path=/", jwtToken, 60 * 60);

	        response.setHeader("Access-Control-Allow-Credentials", "true");
	        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
	        response.setHeader("Set-Cookie", cookieHeader);

	        return ResponseEntity.ok(authResponse);
	    } catch (Exception e) {
	        return ("Bad credentials").equals(e.getMessage()) ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}



	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
		ResponseEntity<AuthResponse> response = null;
		try {
			response = ResponseEntity.ok(authService.register(request));
		} catch (Exception e) {
			response = new ResponseEntity<AuthResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
		return response;
	}
	
	@PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response, @CookieValue(name = "jwtToken") String jwtToken) {
        try {
            
	        String cookieHeader = String.format("jwtToken=%s; SameSite=None; Secure; HttpOnly; Max-Age=%d; Path=/", "", 60 * 60);

	        response.setHeader("Access-Control-Allow-Credentials", "true");
	        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
	        response.setHeader("Set-Cookie", cookieHeader);

            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

}
