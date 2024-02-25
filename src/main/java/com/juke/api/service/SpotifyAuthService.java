package com.juke.api.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juke.api.model.AccessTokenResponse;
import com.juke.api.repository.IAccessTokenResponseRepository;
import com.juke.api.utils.AdminConfigurationConstants;
import com.juke.api.utils.SystemLogger;

@Service
public class SpotifyAuthService implements IOAuthHandler {
	
	
    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_ID}")
    private String CLIENT_ID;
    
    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET}")
    private String CLIENT_SECRET;
    
    @Autowired
    private IAccessTokenResponseRepository accessTokenResponseRepository;

    @Value("${SPOTIFY_AUTH_REDIRECT_URL}")
    private String SPOTIFY_AUTH_REDIRECT_URL;
    
    @Value("${CLIENT_URL_ADMIN_PANEL}")
    private String CLIENT_URL_ADMIN_PANEL;
    
    private RestTemplate restTemplate = new RestTemplate();
	
    private AccessTokenResponse requestAccessTokenAndRefreshToken(String authorizationCode) throws IOException {
        String tokenEndpoint = "https://accounts.spotify.com/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", authorizationCode);
        requestBody.add("redirect_uri", SPOTIFY_AUTH_REDIRECT_URL);
        requestBody.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return extractAccessTokenAndRefreshToken(responseEntity.getBody());
        } else {
            throw new IOException("Failed to request access token. Response code: " + responseEntity.getStatusCode());
        }
    }

	
    private AccessTokenResponse extractAccessTokenAndRefreshToken(String responseString) {
    	String accessToken = null;
    	String refreshToken = null;
    	Timestamp expirationTime = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseString);
            accessToken = jsonNode.get("access_token").asText();
            
            if(jsonNode.get("refresh_token") != null) {
            	refreshToken = jsonNode.get("refresh_token").asText();
            }
            
            expirationTime = calculateExpirationTime();
            
        } catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
        }
        return new AccessTokenResponse(accessToken, refreshToken, expirationTime, AdminConfigurationConstants.ACCESS_TOKEN_RESPONSE_SERVICE_ID_SPOTIFY_SDK);
    }
    
    @Override
    public String buildAuthorizationUrl(String state) throws IOException {
        String scope = "user-read-playback-state user-modify-playback-state playlist-modify-public playlist-modify-private";

        String authEndpoint = "https://accounts.spotify.com/authorize";
        String queryParams = String.format("response_type=code&client_id=%s&scope=%s&redirect_uri=%s&state=%s",
                URLEncoder.encode(CLIENT_ID, "UTF-8"),
                URLEncoder.encode(scope, "UTF-8"),
                URLEncoder.encode(SPOTIFY_AUTH_REDIRECT_URL, "UTF-8"),
                URLEncoder.encode(state, "UTF-8"));

        return authEndpoint + "?" + queryParams;
    }

    public String generateRandomString(int length) {
        String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(possibleChars.charAt(random.nextInt(possibleChars.length())));
        }
        return randomString.toString();
    }
    
    private AccessTokenResponse refreshAccessToken(String refreshToken) throws IOException {
        String tokenEndpoint = "https://accounts.spotify.com/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", URLEncoder.encode(refreshToken, "UTF-8"));

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return extractAccessTokenAndRefreshToken(responseEntity.getBody());
        } else {
            throw new IOException("Failed to refresh access token. Response code: " + responseEntity.getStatusCode());
        }
    }
    
    //We always save one row by AccessTokenResponse serviceId
    @Override
    public RedirectView saveAccesTokenAndRefreshToken(String code) {
    	AccessTokenResponse newTokenResponse;
    	Optional<AccessTokenResponse> storedTokenOptional;
    	RedirectView response = null;
		try {
			newTokenResponse = requestAccessTokenAndRefreshToken(code);
			storedTokenOptional = accessTokenResponseRepository.findByServiceId(AdminConfigurationConstants.ACCESS_TOKEN_RESPONSE_SERVICE_ID_SPOTIFY_SDK);
			if (storedTokenOptional.isPresent()){
				//Update values
				AccessTokenResponse storedToken = storedTokenOptional.get();
				storedToken.setAccessToken(newTokenResponse.getAccessToken());
				storedToken.setRefreshToken(newTokenResponse.getRefreshToken());
				storedToken.setExpirationTime(newTokenResponse.getExpirationTime());
				accessTokenResponseRepository.save(storedToken);
			} else {
		    	accessTokenResponseRepository.save(newTokenResponse); //TODO encrypt and decrypt
			}
	    	response = new RedirectView(CLIENT_URL_ADMIN_PANEL);
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			response = new RedirectView(CLIENT_URL_ADMIN_PANEL + "?error=No se pudo vincular la cuenta de Spotify\"");
		}
		return response;   	 
    }
    
    private Timestamp calculateExpirationTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + 3500000; // almost 1 hour in milliseconds
        return new Timestamp(expirationTimeMillis);
    }
    
    /*
    *
    * This method works when the admin was logged succesfully and generated the first row with valid and refresh token. After that, it will refresh if necessary
    */
    @Override
	public String getToken() throws Exception {
		Optional<AccessTokenResponse> optionalToken = accessTokenResponseRepository.findByServiceId(AdminConfigurationConstants.ACCESS_TOKEN_RESPONSE_SERVICE_ID_SPOTIFY_SDK);
		Timestamp currentTimeMillis = new Timestamp(System.currentTimeMillis());
		String token = null;

		try {
			if (optionalToken.isPresent()) {
				AccessTokenResponse storedToken = optionalToken.get();
				if (storedToken.getExpirationTime().before(currentTimeMillis)) {
					AccessTokenResponse newToken = refreshAccessToken(storedToken.getRefreshToken());
					storedToken.setAccessToken(newToken.getAccessToken());
			    	accessTokenResponseRepository.save(storedToken);
				} 
				token = storedToken.getAccessToken();
			} else {
				throw new Exception("No se pudo obtener el token de Spotify");
			}
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw new Exception(e);
		}
		
		return token;
	}


}
