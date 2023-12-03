package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.juke.api.model.AccessTokenResponse;
import com.juke.api.service.SpotifyAuthService;

import java.io.IOException;

@Controller
@RequestMapping("/spotify")
public class SpotifyController {

    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_ID}")
    private String CLIENT_ID;
    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET}")
    private String CLIENT_SECRET;
    
    @Autowired
    private SpotifyAuthService spotifyAuthService;

    public static String accessToken;
    public static String refreshToken;  // store

    /*This method will redirect the user to spotify login */
    @GetMapping("/login")
    public RedirectView login() throws IOException {
        String state = spotifyAuthService.generateRandomString(16);
        String authorizationUrl = spotifyAuthService.buildAuthorizationUrl(state);
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, @RequestParam("state") String state) throws IOException {
        AccessTokenResponse tokenResponse = spotifyAuthService.requestAccessTokenAndRefreshToken(code, state);//TODO change for saveAccesTokenAndRefreshToken
        accessToken = tokenResponse.getAccessToken();
        refreshToken = tokenResponse.getRefreshToken();
        return "redirect:/";
    }

}
