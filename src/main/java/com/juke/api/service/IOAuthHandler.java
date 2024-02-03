package com.juke.api.service;


import java.io.IOException;

import org.springframework.web.servlet.view.RedirectView;


public interface IOAuthHandler {
	
	public RedirectView saveAccesTokenAndRefreshToken(String code);
	public String getToken() throws Exception;
    public String buildAuthorizationUrl(String state) throws IOException;
}
