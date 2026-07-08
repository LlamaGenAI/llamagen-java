package com.llamagen;

import com.llamagen.http.HttpTransport;
import com.llamagen.http.JavaNetHttpTransport;
import com.llamagen.service.AnimationService;
import com.llamagen.service.ComicService;
import java.time.Duration;
import java.util.Objects;

public final class LlamaGenClient {
    public static final String DEFAULT_BASE_URL = "https://api.llamagen.ai/v1";

    private final String apiKey;
    private final String baseUrl;
    private final int maxNetworkRetries;
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final HttpTransport httpTransport;
    private final ComicService comicService;
    private final AnimationService animationService;

    public LlamaGenClient(String apiKey) {
        this(builder().setApiKey(apiKey));
    }

    private LlamaGenClient(Builder builder) {
        this.apiKey = requireNonBlank(builder.apiKey, "apiKey");
        this.baseUrl = trimTrailingSlash(builder.baseUrl == null ? DEFAULT_BASE_URL : builder.baseUrl);
        this.maxNetworkRetries = builder.maxNetworkRetries;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.httpTransport = builder.httpTransport == null
                ? new JavaNetHttpTransport(connectTimeout)
                : builder.httpTransport;
        ApiResource apiResource = new ApiResource(
                apiKey,
                baseUrl,
                maxNetworkRetries,
                connectTimeout,
                readTimeout,
                httpTransport);
        this.comicService = new ComicService(apiResource);
        this.animationService = new AnimationService(apiResource);
    }

    public static Builder builder() {
        return new Builder();
    }

    public ComicService comic() {
        return comicService;
    }

    public ComicService comics() {
        return comicService;
    }

    public AnimationService animation() {
        return animationService;
    }

    public AnimationService animations() {
        return animationService;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getMaxNetworkRetries() {
        return maxNetworkRetries;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value;
    }

    private static String trimTrailingSlash(String value) {
        Objects.requireNonNull(value, "baseUrl");
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public static final class Builder {
        private String apiKey;
        private String baseUrl = DEFAULT_BASE_URL;
        private int maxNetworkRetries = 2;
        private Duration connectTimeout = Duration.ofSeconds(30);
        private Duration readTimeout = Duration.ofSeconds(80);
        private HttpTransport httpTransport;

        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setMaxNetworkRetries(int maxNetworkRetries) {
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

        public Builder setHttpTransport(HttpTransport httpTransport) {
            this.httpTransport = httpTransport;
            return this;
        }

        public LlamaGenClient build() {
            return new LlamaGenClient(this);
        }
    }
}
