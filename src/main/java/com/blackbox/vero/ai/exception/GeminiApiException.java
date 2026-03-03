package com.blackbox.vero.ai.exception;

/**
 * Exception thrown when there's an error communicating with the Gemini API.
 */
public class GeminiApiException extends RuntimeException {

    public GeminiApiException(String message) {
        super(message);
    }

    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
