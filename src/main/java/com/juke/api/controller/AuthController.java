package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juke.api.dto.AuthResponse;
import com.juke.api.dto.LoginRequest;
import com.juke.api.dto.RegisterRequest;
import com.juke.api.service.AuthService;
import com.juke.api.utils.SystemLogger;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	
    @Value("${ALLOWED_ORIGINS}")
    String allowedOrigin;


	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
	    try {
	        AuthResponse authResponse = authService.login(request);
	        /*  String jwtToken = authResponse.getToken();

	      /*  String cookieHeader = String.format("jwtToken=%s; SameSite=None; HttpOnly; Secure; Max-Age=%d; Path=/", jwtToken, 180 * 24 * 60 * 60); //6 months

	        response.setHeader("Access-Control-Allow-Credentials", "true");
	        response.setHeader("Access-Control-Allow-Origin", allowedOrigin);
	        response.setHeader("Set-Cookie", cookieHeader);
*/
	        return ResponseEntity.ok(authResponse);
	    } catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
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
			SystemLogger.error(e.getMessage(), e);
		}
		return response;
	}
	
	@PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response, @CookieValue(name = "jwtToken") String jwtToken) {
        try {
            
	        String cookieHeader = String.format("jwtToken=%s; SameSite=None; Secure; HttpOnly; Max-Age=%d; Path=/", "", 60 * 60);

	        response.setHeader("Access-Control-Allow-Credentials", "true");
	        response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Set-Cookie", cookieHeader);

            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("test");
	}

}
