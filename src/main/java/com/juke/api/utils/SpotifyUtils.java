package com.juke.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyUtils {

	public static final Integer PLAYLIST_UPDATE_OFFSET_LIMIT = 300;

	public static String extractSpotifyId(String spotifyUri) {
		String result = spotifyUri;

		// Check if the input is already a Spotify ID (alphanumeric)
		if (!spotifyUri.matches("[a-zA-Z0-9]+")) {

			// Pattern to match Spotify playlist URIs
			Pattern pattern = Pattern.compile("https://open\\.spotify\\.com/playlist/([a-zA-Z0-9]+).*");
			Matcher matcher = pattern.matcher(spotifyUri);

			// Check if the input matches the pattern
			if (matcher.matches()) {
				result = matcher.group(1); // Return the Spotify playlist ID
			} else {
				throw new IllegalArgumentException("Invalid Spotify URI");
			}
		}

		return result;
	}

}
