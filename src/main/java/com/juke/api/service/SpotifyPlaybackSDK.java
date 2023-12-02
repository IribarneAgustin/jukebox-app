package com.juke.api.service;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.JsonArray;
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

			//TO DO: handle exception advising of open the app
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

}
