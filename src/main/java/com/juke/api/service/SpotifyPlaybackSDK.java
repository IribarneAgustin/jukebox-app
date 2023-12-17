package com.juke.api.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.miscellaneous.Device;

@Service
public class SpotifyPlaybackSDK {


    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_ID}")
    private String SPOTIFY_PLAYBACK_SDK_CLIENT_ID;

    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET}")
    private String SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET;
    
    @Value("${SPOTIFY_PLAYLIST_ID}")
    private String SPOTIFY_PLAYLIST_ID;

	public void playSong(String trackUri, String token) {
		try {
			
			SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(SPOTIFY_PLAYBACK_SDK_CLIENT_ID)
					.setClientSecret(SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET).build();

			spotifyApi.setAccessToken(token);

			//TODO: handle exception advising of open the app
			Device[] devices = spotifyApi.getUsersAvailableDevices().build().execute();
			String deviceId = devices[0].getId();

			JsonArray json = new JsonArray();
			json.add(trackUri);

			Future<String> playbackFuture = spotifyApi.startResumeUsersPlayback().uris(json).device_id(deviceId).build()
					.executeAsync();

			playbackFuture.get();
			System.out.println("Playback started!");

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void addTracksToPlaylist(String uri, String accessToken) {
	    try {
	        List<String> trackUris = new ArrayList<>();
	        trackUris.add(uri);
	        //String playlistId = "62xLZGP19lvuEewLMYwHV1";

	        disableShuffle(accessToken);
	        String apiUrl = "https://api.spotify.com/v1/playlists/" + SPOTIFY_PLAYLIST_ID + "/tracks";

	        // Prepare the JSON payload using GSON
	        JsonObject jsonPayload = new JsonObject();
	        jsonPayload.add("uris", new JsonArray());
	        for (String trackUri : trackUris) {
	            jsonPayload.getAsJsonArray("uris").add(new JsonPrimitive(trackUri));
	        }

	        // Convert the JSON payload to a string
	        String jsonPayloadString = new Gson().toJson(jsonPayload);

	        // Create the HTTP client
	        HttpClient httpClient = HttpClient.newHttpClient();

	        // Build the HTTP request
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(apiUrl))
	                .header("Authorization", "Bearer " + accessToken)
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(jsonPayloadString))
	                .build();

	        // Send the HTTP request and receive the response
	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        // Check the HTTP status code
	        if (response.statusCode() == 201) {
	            System.out.println("Track added to the playlist!");
	        } else {
	            System.out.println("Error: " + response.body());
	        }

	    } catch (Exception e) {
	        System.out.println("Error: " + e.getMessage());
	    }
	}
	
    public void enqueueTrack(String trackUri, String token) {
        try {
			SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(SPOTIFY_PLAYBACK_SDK_CLIENT_ID)
					.setClientSecret(SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET).build();
			
			spotifyApi.setAccessToken(token);
			Device[] devices = spotifyApi.getUsersAvailableDevices().build().execute();
			String deviceId = devices[0].getId();
        	
            String apiUrl = "https://api.spotify.com/v1/me/player/queue?uri=" + trackUri +
                            "&device_id=" + deviceId;

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                System.out.println("Track enqueued!");
            } else {
                System.out.println("Error: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void disableShuffle(String accessToken) throws Exception {
        String apiUrl = "https://api.spotify.com/v1/me/player/shuffle?state=false";

        // Create the HTTP client
        HttpClient httpClient = HttpClient.newHttpClient();

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + accessToken)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        // Send the HTTP request and receive the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the HTTP status code
        if (response.statusCode() == 204) {
            System.out.println("Shuffle disabled!");
        } else {
            System.out.println("Error disabling shuffle: " + response.body());
        }
    }


}
