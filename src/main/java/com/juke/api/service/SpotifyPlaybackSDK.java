package com.juke.api.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.juke.api.utils.SystemLogger;

import io.jsonwebtoken.lang.Arrays;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.miscellaneous.Device;

@Service
public class SpotifyPlaybackSDK {


    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_ID}")
    private String SPOTIFY_PLAYBACK_SDK_CLIENT_ID;

    @Value("${SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET}")
    private String SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET;

	public void playSong(String trackUri, String token) {
		try {
			
			SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(SPOTIFY_PLAYBACK_SDK_CLIENT_ID)
					.setClientSecret(SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET).build();

			spotifyApi.setAccessToken(token);

			Device[] devices = spotifyApi.getUsersAvailableDevices().build().execute();
			if(devices == null || (devices != null && devices.length < 1)) {
				throw new Exception("Spotify App is not opened");
			}
			String deviceId = getDeviceId(devices);
			
			JsonArray json = new JsonArray();
			json.add(trackUri);

			Future<String> playbackFuture = spotifyApi.startResumeUsersPlayback().uris(json).device_id(deviceId).build().executeAsync();

			playbackFuture.get();
			SystemLogger.info("Playback started!");

		} catch (Exception e) {
			SystemLogger.error("Error: " + e.getMessage(),e);
		}
	}
	
	public void addTrackToPlaylist(String trackUri, String accessToken, String spotifyPlaylistID) throws Exception {
	    try {
	        String apiUrl = "https://api.spotify.com/v1/playlists/" + spotifyPlaylistID + "/tracks";

	        // Prepare the JSON payload using GSON
	        JsonObject jsonPayload = new JsonObject();
	        jsonPayload.add("uris", new JsonArray());
	        jsonPayload.getAsJsonArray("uris").add(new JsonPrimitive(trackUri));

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
	        	SystemLogger.info("Track added to the playlist!");
	        } else {
	        	SystemLogger.info("Error: " + response.body());
	        }

	    } catch (Exception e) {
	    	//We dont throw exception because is not mandatory
	    	SystemLogger.error("Error: " + e.getMessage(),e);
	    }
	}
	
    public void enqueueTrack(String trackUri, String token) throws Exception {
        try {
			SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(SPOTIFY_PLAYBACK_SDK_CLIENT_ID)
					.setClientSecret(SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET).build();
			
			spotifyApi.setAccessToken(token);

			Device[] devices = spotifyApi.getUsersAvailableDevices().build().execute();
			if(devices == null || (devices != null && devices.length < 1)) {
				throw new Exception("Spotify App is not opened");
			}
			
			String deviceId = getDeviceId(devices);
        	
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
            	SystemLogger.info("Track enqueued!");
            } else {
	        	throw new Exception("Ocurrió un error al encolar la canción en spotify");
            }

        } catch (Exception e) {
        	SystemLogger.error("Error: " + e.getMessage(), e);
        	throw e;
        }
    }
    
    /*
     * Spotify allows only one device playing.
     * Match by Active, if none is active, is the same because no playing will be interrupted.
     */
	private String getDeviceId(Device[] devices) {
		String deviceId = null;
		if (devices.length > 1) {
			for (Device device : devices) {
				if (device.getIs_active()) {
					deviceId = device.getId();
				}
			}
		}
		// None active OR only one available
		if (deviceId == null) {
			deviceId = devices[0].getId();
		}

		return deviceId;
	}
    
}
