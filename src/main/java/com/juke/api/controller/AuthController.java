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
	        
	        Cookie cookie = new Cookie("jwtToken", jwtToken);
	        response.addCookie(cookie);
	        cookie.setSecure(true);  // If served over HTTPS
	        cookie.setHttpOnly(true);
	        response.addCookie(cookie);
	        
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

            Cookie cookie = new Cookie("jwtToken", null);
            cookie.setMaxAge(0);
            cookie.setSecure(true);  // If served over HTTPS
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

}
