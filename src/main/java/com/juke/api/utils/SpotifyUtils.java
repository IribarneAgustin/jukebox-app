package com.juke.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyUtils {
	
	public static final Integer PLAYLIST_UPDATE_OFFSET_LIMIT = 300;
	
	public static String extractSpotifyId(String spotifyUri) {
		String result = spotifyUri;
		
		// Check if the input is already a Spotify ID (alphanumeric)
		if (!spotifyUri.matches("[a-zA-Z0-9]+")) {

			// Pattern to match Spotify URIs
			Pattern pattern = Pattern.compile("spotify:(track|album|artist):([a-zA-Z0-9]+)");
			Matcher matcher = pattern.matcher(spotifyUri);

			// Check if the input matches the pattern
			if (matcher.matches()) {
				return matcher.group(2); // Return the Spotify ID
			} else {
				throw new IllegalArgumentException("Invalid Spotify URI");
			}
		}

		return result;
	}


}
