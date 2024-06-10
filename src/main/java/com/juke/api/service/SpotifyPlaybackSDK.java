package com.juke.api.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Future;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.juke.api.model.TrackOrder;
import com.juke.api.utils.OrderState;
import com.juke.api.utils.SystemLogger;

import io.jsonwebtoken.lang.Arrays;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
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
	
	//Spotify API doesnt supports multiples connections with the app, so when more than once user make this request at the same time, it thorws an errors.
	//To avoid this error, we implement re attempts mechanism
	public void enqueueTrack(String trackUri, String token) throws Exception {
		Integer attempts = 0;
		Boolean successConnection = Boolean.FALSE;
		while (attempts < 3 && !successConnection) {
			try {
				SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(SPOTIFY_PLAYBACK_SDK_CLIENT_ID)
						.setClientSecret(SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET).build();

				spotifyApi.setAccessToken(token);

				Device[] devices = spotifyApi.getUsersAvailableDevices().build().execute();
				if (devices == null || (devices != null && devices.length < 1)) {
					throw new Exception("Spotify App is not opened");
				}

				String deviceId = getDeviceId(devices);

				String apiUrl = "https://api.spotify.com/v1/me/player/queue?uri=" + trackUri + "&device_id=" + deviceId;

				HttpClient httpClient = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl))
						.header("Authorization", "Bearer " + token).POST(HttpRequest.BodyPublishers.noBody()).build();

				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 204) {
					successConnection = Boolean.TRUE;
					SystemLogger.info("Track enqueued!");
				} else {
					throw new Exception("Ocurrió un error al encolar la canción en spotify");
				}

			} catch (Exception e) {
				SystemLogger.error("Error: " + e.getMessage(), e);
				SystemLogger.info("Connection failed with Spotify API trying to enqueue, re attempting...");
			} finally {
				Thread.sleep(3000);
				attempts++;
			}
		}
	}
    /*
     * Spotify allows only one device playing.
     * Match by Active, if none is active, is the same because no playing will be interrupted.
     */
    //Spotify doesn't allow the same account playing different songs in different devices at the same time.
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
	
	//Spotify API doesnt supports multiples connections with the app, so when more than once user make this request at the same time, it thorws an errors.
	//To avoid this error, we implement re attempts mechanism
	public Boolean isSpotifyDeviceOpen(String authToken) throws ParseException, SpotifyWebApiException, IOException, InterruptedException {
		Integer attempts = 0;
		Boolean successConnection = Boolean.FALSE;
		
		Boolean result = Boolean.TRUE;
		SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(SPOTIFY_PLAYBACK_SDK_CLIENT_ID).setClientSecret(SPOTIFY_PLAYBACK_SDK_CLIENT_SECRET).build();
		spotifyApi.setAccessToken(authToken);
		while (attempts < 3 && !successConnection) {
			try {
				Device[] devices = spotifyApi.getUsersAvailableDevices().build().execute();
				if (devices == null || (devices != null && devices.length < 1)) {
					result = Boolean.FALSE;
				}
				successConnection = Boolean.TRUE;
			} catch (Exception e) {
				SystemLogger.info("Connection failed with Spotify API, re attempting...");
			} finally {
				Thread.sleep(2000);
				attempts++;
			}
		}

		return result;
	}

    
}
