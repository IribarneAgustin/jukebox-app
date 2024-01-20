package com.juke.api.utils;

import java.util.Random;

public class AuthUtils {
	
	public static String generateRandomString(int length) {
		String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder randomString = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			randomString.append(possibleChars.charAt(random.nextInt(possibleChars.length())));
		}
		return randomString.toString();
	}
}
