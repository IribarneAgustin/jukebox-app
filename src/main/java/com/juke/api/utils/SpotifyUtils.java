package com.juke.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyUtils {
	
    public static String extractSpotifyId(String spotifyUri) {

        Pattern pattern = Pattern.compile("spotify:(track|album|artist):([a-zA-Z0-9]+)");
        Matcher matcher = pattern.matcher(spotifyUri);

        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            throw new IllegalArgumentException("Invalid Spotify URI");
        }
    }

}
