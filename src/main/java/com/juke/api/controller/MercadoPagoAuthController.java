package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.juke.api.service.MercadoPagoAuthService;

@RestController
@RequestMapping("/api/mp/")
public class MercadoPagoAuthController {
	
	@Autowired
	private MercadoPagoAuthService mercadoPagoAuthService;
	
	/*
	 * SHOULD RUN THIS ENDPOINT FROM SELLER ACOUNT TO LINKED IT WITH MARKETPLACE AND GET TOKEN 
	 */
    @GetMapping("/login")
    public ResponseEntity<String> login() throws Exception {
        String authorizationUrl = mercadoPagoAuthService.buildAuthorizationUrl(null);
        return ResponseEntity.ok(authorizationUrl);
    }
	
	/*
	 * ENDPOINT SET IN MARKETPLACE APP ACCOUNT
	 */
	@GetMapping("/auth/callback")
	public RedirectView authorizeMercadoPago(@RequestParam("code") String code) {
    	RedirectView response = mercadoPagoAuthService.saveAccesTokenAndRefreshToken(code);
        return response;
	}
	

}
