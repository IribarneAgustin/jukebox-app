package com.juke.api.utils;

public class TrackEnqueueException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TrackEnqueueException(String message) {
        super(message);
    }

    public TrackEnqueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
