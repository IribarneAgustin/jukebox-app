package com.juke.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

@Service
public class MercadoPagoAuthService implements IOAuthHandler{
	
    @Autowired
    private IAccessTokenResponseRepository accessTokenResponseRepository;
    
	@Value("${MERCADO_PAGO_CLIENT_ID}")
	private String CLIENT_ID;
	@Value("${MERCADO_PAGO_CLIENT_SECRET}")
	private String CLIENT_SECRET;
	@Value("${CLIENT_ROOT_URL}")
	private String CLIENT_ROOT_URL;
	@Value("${SERVER_ROOT_URL}")
	private String SERVER_ROOT_URL;
	
    @Value("${MERCADO_PAGO_MARKETPLACE_REDIRECT_URL}")
    private String MERCADO_PAGO_MARKETPLACE_REDIRECT_URL;


	private final String CLIENT_URL_ADMIN_PANEL = "http://localhost:3000/admin/dashboard"; //TODO add succesfull message
	private final String CLIENT_URL_LOGIN_ERROR = "http://localhost:3000/admin/login?error=No se pudo conectar con Mercado Pago";
    
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
	    	response = new RedirectView(CLIENT_URL_ADMIN_PANEL);
		} catch (Exception e) {
			e.printStackTrace();
			response = new RedirectView(CLIENT_URL_LOGIN_ERROR);
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
            e.printStackTrace();
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
			e.printStackTrace();
			throw new Exception(e);
		}
		
		return token;
	}
	
    private AccessTokenResponse refreshAccessToken(String refreshToken) throws IOException {
        String tokenEndpoint = "https://api.mercadopago.com/oauth/token";
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        String requestBody = String.format("grant_type=refresh_token&refresh_token=%s", URLEncoder.encode(refreshToken, "UTF-8"));

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
                return extractAccessTokenAndRefreshToken(response.toString());
            }
        } else {
            throw new IOException("Failed to refresh access token. Response code: " + responseCode);
        }
    }
    
    @Override
    public String buildAuthorizationUrl(String state) {
        return "https://auth.mercadopago.com.ar/authorization" +
                "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&platform_id=mp" +
                "&redirect_uri=" + MERCADO_PAGO_MARKETPLACE_REDIRECT_URL;
    }

}
