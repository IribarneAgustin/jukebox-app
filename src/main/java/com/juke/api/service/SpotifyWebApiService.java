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
import com.juke.api.utils.SystemLogger;

@Service
public class SpotifyWebApiService {

	@Value("${SPOTIFY_WEB_API_CLIENT_SECRET}")
	private String SPOTIFY_WEB_API_CLIENT_SECRET;

	@Value("${SPOTIFY_WEB_API_CLIENT_ID}")
	private String SPOTIFY_WEB_API_CLIENT_ID;

	public static final String SPOTIFY_WEB_API_TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token";
	private String accessToken;
	private Instant tokenExpirationTime;
	private RestTemplate restTemplate = new RestTemplate();

	private String getAccessToken() throws Exception {
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

			restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(SPOTIFY_WEB_API_TOKEN_ENDPOINT,
					requestEntity, String.class);

			if (responseEntity != null) {
				JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
				accessTokenResponse = jsonNode.get("access_token").asText();
				tokenExpirationTime = Instant.now().plusSeconds(3500);
			}
		} catch (Exception e) {
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}

		return accessTokenResponse;
	}

	private String getOrRefreshAccessToken() throws Exception {
		if (accessToken == null || isTokenExpired()) {
			accessToken = getAccessToken();
		}
		return accessToken;
	}

	private boolean isTokenExpired() {
		return tokenExpirationTime == null ? Boolean.TRUE : Instant.now().isAfter(tokenExpirationTime);
	}

	
	public ResponseEntity<String> getTrackInformationByName(String trackOrArtistName) throws Exception {
	    HttpHeaders headers = new HttpHeaders();

	    headers.set("Authorization", "Bearer " + getOrRefreshAccessToken());
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    
	    String url = "https://api.spotify.com/v1/search?q=" + trackOrArtistName + "&type=track,artist&sort=popularity&limit=5";

	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

	    return response;
	}


	public List<String> getPlaylistIdsForCountry(String country) throws Exception {
		List<String> playlistIds = new ArrayList<>();
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
			SystemLogger.error(e.getMessage(), e);
			throw e;
		}

		return playlistIds;
	}


	public List<Track> getPlaylistTracks(String playlistId) throws Exception {
	    List<Track> tracks = new ArrayList<>();
	    ObjectMapper objectMapper = new ObjectMapper();

	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + getOrRefreshAccessToken());
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        String url = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

	        if (response.getStatusCode().is2xxSuccessful()) {
	            JsonNode jsonNode = objectMapper.readTree(response.getBody());
	            processJsonNode(jsonNode, tracks);
	        } else {
	            SystemLogger.info("Failed to retrieve tracks. Status code: " + response.getStatusCode());
	        }
	    } catch (Exception e) {
	        SystemLogger.error(e.getMessage(), e);
	        throw e;
	    }

	    return tracks;
	}

	private void processJsonNode(JsonNode jsonNode, List<Track> tracks) {
	    if (jsonNode != null && jsonNode.has("items")) {
	        jsonNode.get("items").forEach(item -> {
	            JsonNode trackNode = item.get("track");
	            processTrackNode(trackNode, tracks);
	        });
	    } else {
	    	 SystemLogger.info("Invalid JSON structure: Missing 'items' node");
	    }
	}

	private void processTrackNode(JsonNode trackNode, List<Track> tracks) {
	    if (trackNode != null) {
	        JsonNode albumNode = trackNode.get("album");

	        if (albumNode != null && albumNode.has("images") && albumNode.get("images").isArray() && albumNode.get("images").size() > 0) {
	            String imageUrl = albumNode.get("images").get(0).get("url").asText();

	            String artistName = trackNode.get("artists").get(0).get("name").asText();
	            String trackName = trackNode.get("name").asText();

	            Track track = new Track(
	                    imageUrl,
	                    artistName,
	                    trackName,
	                    trackNode.get("uri").asText(),
	                    artistName + " " + trackName
	            );
	            tracks.add(track);
	        }
	    }
	}




}
