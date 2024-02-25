package com.juke.api.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AuthUtils {
	
    private static final List<String> ALLOWED_URLS = Arrays.asList(
            "/api/auth/login",
            "/ws",
            "/api/mp/auth/callback",
            "/api/spotify/track/list/queue",
            "/api/spotify/track",
            "/api/payment"
    );
	
	public static String generateRandomString(int length) {
		String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder randomString = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			randomString.append(possibleChars.charAt(random.nextInt(possibleChars.length())));
		}
		return randomString.toString();
	}
	

    public static boolean isAllowedUrl(String requestURI) {
        return ALLOWED_URLS.stream().anyMatch(requestURI::equals) || ALLOWED_URLS.stream().anyMatch(requestURI::startsWith);
    }
}
