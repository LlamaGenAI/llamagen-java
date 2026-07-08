package com.llamagen.exception;

public class LlamaGenTimeoutException extends LlamaGenException {
    public LlamaGenTimeoutException(String message) {
        super(message);
    }

    public LlamaGenTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
