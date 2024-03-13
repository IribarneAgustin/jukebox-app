package com.juke.api.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Optional;

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
public class MercadoPagoAuthService implements IOAuthHandler{
	
    @Autowired
    private IAccessTokenResponseRepository accessTokenResponseRepository;
    
	@Value("${MERCADO_PAGO_CLIENT_ID}")
	private String CLIENT_ID;
	
	@Value("${MERCADO_PAGO_CLIENT_SECRET}")
	private String CLIENT_SECRET;

    @Value("${MERCADO_PAGO_MARKETPLACE_REDIRECT_URL}")
    private String MERCADO_PAGO_MARKETPLACE_REDIRECT_URL;
    
    @Value("${CLIENT_URL_ADMIN_PANEL}")
	private String CLIENT_URL_ADMIN_PANEL;
    
    private RestTemplate restTemplate = new RestTemplate();
    
	@Override
    public RedirectView saveAccesTokenAndRefreshToken(String code) {
    	AccessTokenResponse newTokenResponse;
    	Optional<AccessTokenResponse> storedTokenOptional;
    	RedirectView response = null;
		try {
			newTokenResponse = requestAccessTokenAndRefreshToken(code);
			storedTokenOptional = accessTokenResponseRepository.findByServiceId(AdminConfigurationConstants.ACCESS_TOKEN_RESPONSE_SERVICE_ID_MERCADO_PAGO);
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
	    	response = new RedirectView(CLIENT_URL_ADMIN_PANEL + "?message=Cuenta vinculada con Mercado Pago correctamente");
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			response = new RedirectView(CLIENT_URL_ADMIN_PANEL + "?error=No se pudo vincular la cuenta de Mercado Pago");
		}
		return response;   	 
    }

	private AccessTokenResponse requestAccessTokenAndRefreshToken(String authorizationCode) throws IOException {
        String tokenEndpoint = "https://api.mercadopago.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", authorizationCode);
        requestBody.add("redirect_uri", MERCADO_PAGO_MARKETPLACE_REDIRECT_URL);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("client_id", CLIENT_ID);

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
        return new AccessTokenResponse(accessToken, refreshToken, expirationTime, AdminConfigurationConstants.ACCESS_TOKEN_RESPONSE_SERVICE_ID_MERCADO_PAGO);
    }
    
    private Timestamp calculateExpirationTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + (5L * 30L * 24L * 60L * 60L * 1000L); // 5 months in milliseconds
        return new Timestamp(expirationTimeMillis);
    }
    /*
     * 
     *This method works when the admin was logged succesfully and generated the first row with valid and refresh token. After that, it will refresh if necessary
     *
     */
    @Override
	public String getToken() throws Exception {
		Optional<AccessTokenResponse> optionalToken = accessTokenResponseRepository.findByServiceId(AdminConfigurationConstants.ACCESS_TOKEN_RESPONSE_SERVICE_ID_MERCADO_PAGO);
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
				throw new Exception("No se pudo obtener el token de Mercado Pago");
			}
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw new Exception(e);
		}
		
		return token;
	}
	
    private AccessTokenResponse refreshAccessToken(String refreshToken) throws IOException {
        String tokenEndpoint = "https://api.mercadopago.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", refreshToken);

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
    
    @Override
	public String buildAuthorizationUrl(String state) {
		String response = null;
		try {
		response = String.format("?client_id=%s&response_type=code&platform_id=mp&redirect_uri=%s", 
				URLEncoder.encode(CLIENT_ID, "UTF-8"),
				URLEncoder.encode(MERCADO_PAGO_MARKETPLACE_REDIRECT_URL,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			SystemLogger.error(e.getMessage(), e);
		}
		return response;
	}

}
