package com.juke.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemLogger {

    private static final Logger logger = LoggerFactory.getLogger(SystemLogger.class);

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

}
