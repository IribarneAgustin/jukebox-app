package com.juke.api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juke.api.model.Track;
import com.juke.api.utils.SpotifyUtils;

@Service
public class SpotifyWebApiService {

	@Value("${SPOTIFY_WEB_API_CLIENT_SECRET}")
	private String SPOTIFY_WEB_API_CLIENT_SECRET;

	@Value("${SPOTIFY_WEB_API_CLIENT_ID}")
	private String SPOTIFY_WEB_API_CLIENT_ID;

	public static final String SPOTIFY_WEB_API_TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token";
	private String accessToken;
	private Instant tokenExpirationTime;
	

	private String getAccessToken() {
		String accessTokenResponse = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
			parameters.add("grant_type", "client_credentials");
			parameters.add("client_id", SPOTIFY_WEB_API_CLIENT_ID);
			parameters.add("client_secret", SPOTIFY_WEB_API_CLIENT_SECRET);

			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(SPOTIFY_WEB_API_TOKEN_ENDPOINT,
					requestEntity, String.class);

			if (responseEntity != null) {
				JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
				accessTokenResponse = jsonNode.get("access_token").asText();
				tokenExpirationTime = Instant.now().plusSeconds(3500);
			}
		} catch (Exception e) {
			// TODO logger
			e.printStackTrace();
		}

		return accessTokenResponse;
	}

	private String getOrRefreshAccessToken() {
		if (accessToken == null || isTokenExpired()) {
			accessToken = getAccessToken();
		}
		return accessToken;
	}

	private boolean isTokenExpired() {
		return tokenExpirationTime == null ? Boolean.TRUE : Instant.now().isAfter(tokenExpirationTime);
	}

	public ResponseEntity<String> getArtistInformationByName(String artistName) {
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();

	    headers.set("Authorization", "Bearer " + getOrRefreshAccessToken());
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    
	    String url = "https://api.spotify.com/v1/search?q=" + artistName + "&type=artist&sort=popularity";

	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

	    return response;
	}

	
	public ResponseEntity<String> getTrackInformationByName(String trackOrArtistName) {
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();

	    headers.set("Authorization", "Bearer " + getOrRefreshAccessToken());
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    
	    String url = "https://api.spotify.com/v1/search?q=" + trackOrArtistName + "&type=track,artist&sort=popularity&limit=5";

	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

	    return response;
	}


	public List<String> getPlaylistIdsForCountry(String country) throws Exception {

		List<String> playlistIds = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + getOrRefreshAccessToken());

			HttpEntity<String> entity = new HttpEntity<>(headers);
			Integer offset = 0;
			Integer limit = 50;
			while (offset < SpotifyUtils.PLAYLIST_UPDATE_OFFSET_LIMIT) {
				String url = "https://api.spotify.com/v1/browse/featured-playlists?country=" + country + "&limit=" + limit + "&offset=" + offset;

				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

				if (response.getStatusCode().is2xxSuccessful()) {
					JsonNode jsonNode = objectMapper.readTree(response.getBody());
					jsonNode.get("playlists").get("items").forEach(item -> {
						String playlistId = item.get("id").asText();
						playlistIds.add(playlistId);
					});
				} else {
					System.err.println("Failed to retrieve playlists. Status code: " + response.getStatusCode());
				}
				offset += limit;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return playlistIds;
	}


    public List<Track> getPlaylistTracks(String playlistId) {
        List<Track> tracks = new ArrayList<>();
	    RestTemplate restTemplate = new RestTemplate();
		ObjectMapper objectMapper = new ObjectMapper();
		
        try {
            HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + getOrRefreshAccessToken());
	        
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                jsonNode.get("items").forEach(item -> {
                    JsonNode trackNode = item.get("track");
                    String artistName = trackNode.get("artists").get(0).get("name").asText();
                    String trackName = trackNode.get("name").asText();

                    // Replace underscores with spaces in artistName and trackName
                    artistName = artistName.replace("_", " ");
                    trackName = trackName.replace("_", " ");

                    Track track = new Track(
                            trackNode.get("album").get("images").get(0).get("url").asText(),
                            artistName,
                            trackName,
                            trackNode.get("uri").asText(),
                            artistName + " " + trackName
                    );
                    tracks.add(track);
                });

            } else {
                System.err.println("Failed to retrieve tracks. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tracks;
    }




}
