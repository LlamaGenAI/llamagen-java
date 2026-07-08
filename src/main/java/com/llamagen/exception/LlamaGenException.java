package com.llamagen.exception;

public class LlamaGenException extends Exception {
    private final Integer statusCode;
    private final String requestId;
    private final String responseBody;

    public LlamaGenException(String message) {
        this(message, null, null, null, null);
    }

    public LlamaGenException(String message, Throwable cause) {
        this(message, null, null, null, cause);
    }

    public LlamaGenException(
            String message,
            Integer statusCode,
            String requestId,
            String responseBody,
            Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.requestId = requestId;
        this.responseBody = responseBody;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
