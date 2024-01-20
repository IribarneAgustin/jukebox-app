package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.juke.api.service.SpotifyAuthService;
import com.juke.api.utils.AuthUtils;

@RestController
@RequestMapping("/api/spotify")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:8080", "http://localhost:5173", "https://accounts.spotify.com", "*" })
public class SpotifyAuthController {

   
    @Autowired
    private SpotifyAuthService spotifyAuthService;


    /*This method will redirect the user to spotify login if necessary */
    //Dispatched by the client redirection after login successfully completed
    @GetMapping("/login")
    public RedirectView login() throws Exception {
        String state = AuthUtils.generateRandomString(16);//spotifyAuthService.generateRandomString(16);
        String authorizationUrl = spotifyAuthService.buildAuthorizationUrl(state);
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
    	RedirectView response = spotifyAuthService.saveAccesTokenAndRefreshToken(code, state);
        return response;
    }



}
