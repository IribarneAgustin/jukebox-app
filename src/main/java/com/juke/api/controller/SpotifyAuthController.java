package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.juke.api.service.SpotifyAuthService;
import com.juke.api.utils.AuthUtils;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyAuthController {

   
    @Autowired
    private SpotifyAuthService spotifyAuthService;


    /*This method will redirect the user to spotify login if necessary */
    //Dispatched by the client redirection after login successfully completed
    @GetMapping("/login")
    public ResponseEntity<String> login() throws Exception {
        String state = AuthUtils.generateRandomString(16);
        String authorizationUrl = spotifyAuthService.buildAuthorizationUrl(state);
		return ResponseEntity.ok(authorizationUrl);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
    	RedirectView response = spotifyAuthService.saveAccesTokenAndRefreshToken(code);
        return response;
    }



}
