package com.llamagen.net;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RequestOptions {
    private final String apiKey;
    private final String idempotencyKey;
    private final Integer maxNetworkRetries;
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final Map<String, String> headers;

    private RequestOptions(Builder builder) {
        this.apiKey = builder.apiKey;
        this.idempotencyKey = builder.idempotencyKey;
        this.maxNetworkRetries = builder.maxNetworkRetries;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(builder.headers));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Integer getMaxNetworkRetries() {
        return maxNetworkRetries;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public static final class Builder {
        private String apiKey;
        private String idempotencyKey;
        private Integer maxNetworkRetries;
        private Duration connectTimeout;
        private Duration readTimeout;
        private final Map<String, String> headers = new LinkedHashMap<>();

        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder setIdempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder setMaxNetworkRetries(Integer maxNetworkRetries) {
            this.maxNetworkRetries = maxNetworkRetries;
            return this;
        }

        public Builder setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder putHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(this);
        }
    }
}
