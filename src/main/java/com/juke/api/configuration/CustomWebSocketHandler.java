package com.juke.api.configuration;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.juke.api.utils.SystemLogger;

@Component
public class CustomWebSocketHandler implements WebSocketHandler {
	
	private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	SystemLogger.info("WebSocket connection established: " + session.getId());
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	SystemLogger.info("WebSocket connection closed: " + session.getId() + ", " + closeStatus);
        sessions.remove(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    	SystemLogger.info("Received message: " + message.getPayload());
        for (WebSocketSession webSocketSession : sessions) {
        	webSocketSession.sendMessage(message);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	SystemLogger.error("WebSocket transport error for session: " + session.getId(), exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}
