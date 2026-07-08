package com.llamagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.llamagen.exception.LlamaGenApiException;
import com.llamagen.exception.LlamaGenException;
import com.llamagen.exception.LlamaGenTimeoutException;
import com.llamagen.http.HttpMethod;
import com.llamagen.http.HttpRequest;
import com.llamagen.http.HttpResponse;
import com.llamagen.http.HttpTransport;
import com.llamagen.net.RequestOptions;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiResource {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final String apiKey;
    private final String baseUrl;
    private final int maxNetworkRetries;
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final HttpTransport httpTransport;

    public ApiResource(
            String apiKey,
            String baseUrl,
            int maxNetworkRetries,
            Duration connectTimeout,
            Duration readTimeout,
            HttpTransport httpTransport) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.maxNetworkRetries = maxNetworkRetries;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.httpTransport = httpTransport;
    }

    public <T> T request(
            HttpMethod method,
            String path,
            Map<String, Object> body,
            Map<String, Object> query,
            Class<T> responseClass,
            RequestOptions options) throws LlamaGenException {
        byte[] payload = body == null ? null : GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        return requestBytes(method, path, payload, "application/json", query, responseClass, options);
    }

    public <T> T requestBytes(
            HttpMethod method,
            String path,
            byte[] body,
            String contentType,
            Map<String, Object> query,
            Class<T> responseClass,
            RequestOptions options) throws LlamaGenException {
        RequestOptions resolved = options == null ? RequestOptions.builder().build() : options;
        String requestApiKey = resolved.getApiKey() == null ? apiKey : resolved.getApiKey();
        String url = baseUrl + path + encodeQuery(query);
        int retries = resolved.getMaxNetworkRetries() == null
                ? maxNetworkRetries
                : resolved.getMaxNetworkRetries();
        Duration requestConnectTimeout = resolved.getConnectTimeout() == null
                ? connectTimeout
                : resolved.getConnectTimeout();
        Duration requestReadTimeout = resolved.getReadTimeout() == null
                ? readTimeout
                : resolved.getReadTimeout();

        int attempt = 0;
        while (true) {
            HttpRequest.Builder builder = HttpRequest.builder()
                    .setMethod(method)
                    .setUrl(url)
                    .setConnectTimeout(requestConnectTimeout)
                    .setReadTimeout(requestReadTimeout)
                    .putHeader("Authorization", "Bearer " + requestApiKey)
                    .putHeader("User-Agent", "llamagen-java/0.1.0")
                    .putHeaders(resolved.getHeaders());

            if (contentType != null && body != null) {
                builder.putHeader("Content-Type", contentType);
            }
            if (resolved.getIdempotencyKey() != null) {
                builder.putHeader("Idempotency-Key", resolved.getIdempotencyKey());
            }
            if (body != null) {
                builder.setBody(body);
            }

            try {
                HttpResponse response = httpTransport.send(builder.build());
                if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                    if (responseClass == Void.class) {
                        return null;
                    }
                    return GSON.fromJson(response.getBody(), responseClass);
                }
                if (shouldRetry(response.getStatusCode()) && attempt < retries) {
                    sleepBeforeRetry(attempt);
                    attempt++;
                    continue;
                }
                throw toApiException(response);
            } catch (IOException e) {
                if (attempt < retries) {
                    sleepBeforeRetry(attempt);
                    attempt++;
                    continue;
                }
                throw new LlamaGenException("Network error while calling LlamaGen API", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new LlamaGenTimeoutException("Interrupted while calling LlamaGen API", e);
            }
        }
    }

    public static Gson gson() {
        return GSON;
    }

    private static LlamaGenApiException toApiException(HttpResponse response) {
        String message = "LlamaGen API request failed with status " + response.getStatusCode();
        String body = response.getBody();
        if (body != null && !body.isEmpty()) {
            try {
                JsonObject object = JsonParser.parseString(body).getAsJsonObject();
                if (object.has("message")) {
                    message = object.get("message").getAsString();
                } else if (object.has("error")) {
                    message = object.get("error").toString();
                }
            } catch (RuntimeException ignored) {
                message = body;
            }
        }
        return new LlamaGenApiException(message, response.getStatusCode(), response.getFirstHeader("x-request-id"), body);
    }

    private static boolean shouldRetry(int statusCode) {
        return statusCode == 429 || statusCode >= 500;
    }

    private static void sleepBeforeRetry(int attempt) throws LlamaGenException {
        long delayMillis = Math.min(2000L, 500L * (attempt + 1));
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlamaGenTimeoutException("Interrupted before retrying LlamaGen API request", e);
        }
    }

    private static String encodeQuery(Map<String, Object> query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        Map<String, String> flattened = new LinkedHashMap<>();
        query.forEach((key, value) -> {
            if (value != null) {
                flattened.put(key, String.valueOf(value));
            }
        });
        if (flattened.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder("?");
        boolean first = true;
        for (Map.Entry<String, String> entry : flattened.entrySet()) {
            if (!first) {
                builder.append("&");
            }
            first = false;
            builder.append(urlEncode(entry.getKey())).append("=").append(urlEncode(entry.getValue()));
        }
        return builder.toString();
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 is not supported", e);
        }
    }
}
