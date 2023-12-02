package com.juke.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;

import se.michaelthelin.spotify.SpotifyApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.Future;

@Controller
@RequestMapping("/spotify")
public class SpotifyController {

	@Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_ID}")
    private String CLIENT_ID = "your_spotify_client_id";
	@Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET}")
    private String CLIENT_SECRET;
	
    private static final String REDIRECT_URI = "http://localhost:8080/spotify/callback";
    
	public static String accessToken;
	private Instant tokenExpirationTime;

    @GetMapping("/login")
    public RedirectView login() throws IOException {
        // Step 1: Generate random state
        String state = generateRandomString(16);
        // Step 2: Redirect the user to Spotify login
        String authorizationUrl = buildAuthorizationUrl(state);

        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, @RequestParam("state") String state) throws IOException {
        SpotifyController.accessToken = requestAccessToken(code, state);
        return "redirect:/";
    }

    private String generateRandomString(int length) {
        String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(possibleChars.charAt(random.nextInt(possibleChars.length())));
        }
        return randomString.toString();
    }

    private String buildAuthorizationUrl(String state) throws IOException {
    	String scope = "user-read-playback-state user-modify-playback-state";//"streaming user-read-email user-read-private";

        String authEndpoint = "https://accounts.spotify.com/authorize";
        String queryParams = String.format("response_type=code&client_id=%s&scope=%s&redirect_uri=%s&state=%s",
                URLEncoder.encode(CLIENT_ID, "UTF-8"),
                URLEncoder.encode(scope, "UTF-8"),
                URLEncoder.encode(REDIRECT_URI, "UTF-8"),
                URLEncoder.encode(state, "UTF-8"));

        return authEndpoint + "?" + queryParams;
    }

    private String requestAccessToken(String authorizationCode, String state) throws IOException {
        String tokenEndpoint = "https://accounts.spotify.com/api/token";
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        String requestBody = String.format("code=%s&redirect_uri=%s&grant_type=authorization_code",
                URLEncoder.encode(authorizationCode, "UTF-8"),
                URLEncoder.encode(REDIRECT_URI, "UTF-8"));

        HttpURLConnection connection = (HttpURLConnection) new URL(tokenEndpoint).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        connection.getOutputStream().write(requestBody.getBytes());

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return extractAccessToken(response.toString());
            }
        } else {
            throw new IOException("Failed to request access token. Response code: " + responseCode);
        }
    }
    
    private static String extractAccessToken(String responseString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseString);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
