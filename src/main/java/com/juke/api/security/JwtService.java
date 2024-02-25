package com.juke.api.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Service
public class JwtService {

	private static KeyPair keyPair = generateKeyPair();

	public String getToken(UserDetails user) {
	    Map<String, Object> claims = new HashMap<>();

	    String token = Jwts.builder()
	            .setClaims(claims)
	            .setSubject(user.getUsername())
	            .setIssuedAt(new Date(System.currentTimeMillis()))
	            .setExpiration(new Date(System.currentTimeMillis() + 180 * 24 * 60 * 60)) // 6 months
	            .signWith(keyPair.getPrivate(), SignatureAlgorithm.ES256)
	            .compact();

	    return token;
	}

	 
	private static KeyPair generateKeyPair() {
	    try {
	        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
	        generator.initialize(256);
	        return generator.generateKeyPair();
	    } catch (Exception e) {
	        throw new RuntimeException("Error generating ECDSA key pair", e);
	    }
	}


    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private Claims getAllClaims(String token) {
	    try {
	        // Specify the algorithm when building the parser
	        return Jwts.parserBuilder()
	            .setSigningKey(keyPair.getPublic())
	            .build()
	            .parseClaimsJws(token)
	            .getBody();
	    } catch (Exception e) {
	        throw new RuntimeException("Error parsing JWT claims", e);
	    }
	}


	public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Date getExpiration(String token) {
		return getClaim(token, Claims::getExpiration);
	}

	private boolean isTokenExpired(String token) {
		return getExpiration(token).before(new Date());
	}

}
