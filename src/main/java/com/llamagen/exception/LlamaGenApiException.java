package com.llamagen.exception;

public class LlamaGenApiException extends LlamaGenException {
    public LlamaGenApiException(String message, int statusCode, String requestId, String responseBody) {
        super(message, statusCode, requestId, responseBody, null);
    }
}
