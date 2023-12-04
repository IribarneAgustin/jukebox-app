package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.juke.api.service.SpotifyAuthService;

@RestController
@RequestMapping("admin/spotify")
public class SpotifyController {

   
    @Autowired
    private SpotifyAuthService spotifyAuthService;


    /*This method will redirect the user to spotify login if necessary */
    @GetMapping("/login")
    public RedirectView login() throws Exception {
        String state = spotifyAuthService.generateRandomString(16);
        String authorizationUrl = spotifyAuthService.buildAuthorizationUrl(state);
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
    	ResponseEntity<String> response = spotifyAuthService.saveAccesTokenAndRefreshToken(code, state);
        return response;
    }

}
