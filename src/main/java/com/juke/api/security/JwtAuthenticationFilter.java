package com.juke.api.security;

import java.io.IOException;

import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserDetailsService userDetailsService;

	// This method receive the request with the token inside it, so we extract it
	// and then apply the filters
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = getTokenFromRequest(request);

		if (token == null && request.getRequestURI() != null && !"/api/admin/login".equals(request.getRequestURI()) && !request.getRequestURI().startsWith("/ws") && !request.getRequestURI().startsWith("/api/mp/auth/callback") && !request.getRequestURI().startsWith("/api/spotify/track/list/queue")) {
			token = getTokenFromCookies(request);
			if (token == null) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}
		}

		if (token != null) {
			try {
				String username = jwtService.getUsernameFromToken(token);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);

					if (jwtService.isTokenValid(token, userDetails)) {
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());

						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(authToken);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}
		}
		if (response.getStatus() != HttpStatus.UNAUTHORIZED.value()) {
			filterChain.doFilter(request, response);
		}
	}

	private String getTokenFromRequest(HttpServletRequest request) {

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		String token = null;

		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}

		return token;
	}

	private String getTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String token = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("jwtToken".equals(cookie.getName()) && !cookie.getValue().isEmpty()) {
					token = cookie.getValue();
				}
			}
		}
		return token;
	}

}
