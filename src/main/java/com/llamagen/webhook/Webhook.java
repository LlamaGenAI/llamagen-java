package com.llamagen.webhook;

import com.llamagen.ApiResource;
import com.llamagen.exception.LlamaGenException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Webhook {
    private static final long DEFAULT_TOLERANCE_SECONDS = 300L;

    private Webhook() {}

    public static WebhookEvent constructEvent(String payload, Map<String, List<String>> headers, String secret)
            throws LlamaGenException {
        return constructEvent(payload, headers, secret, DEFAULT_TOLERANCE_SECONDS);
    }

    public static WebhookEvent constructEvent(
            String payload,
            Map<String, List<String>> headers,
            String secret,
            long toleranceSeconds) throws LlamaGenException {
        verifySignature(payload, headers, secret, toleranceSeconds);
        WebhookEvent event = ApiResource.gson().fromJson(payload, WebhookEvent.class);
        String eventId = firstHeader(headers, "X-Llama-Webhook-Id");
        String requestId = firstHeader(headers, "X-Llama-Webhook-Request-Id");
        if (event.getId() == null) {
            event.setId(eventId);
        }
        event.setRequestId(requestId);
        return event;
    }

    public static boolean verifySignature(String payload, Map<String, List<String>> headers, String secret) {
        try {
            verifySignature(payload, headers, secret, DEFAULT_TOLERANCE_SECONDS);
            return true;
        } catch (LlamaGenException e) {
            return false;
        }
    }

    public static void verifySignature(
            String payload,
            Map<String, List<String>> headers,
            String secret,
            long toleranceSeconds) throws LlamaGenException {
        if (secret == null || secret.isEmpty()) {
            throw new LlamaGenException("Webhook secret is required");
        }
        String timestamp = firstHeader(headers, "X-Llama-Webhook-Timestamp");
        String signature = firstHeader(headers, "X-Llama-Webhook-Signature");
        if (timestamp == null || signature == null) {
            throw new LlamaGenException("Missing LlamaGen webhook signature headers");
        }
        long timestampSeconds;
        try {
            timestampSeconds = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            throw new LlamaGenException("Invalid LlamaGen webhook timestamp", e);
        }
        if (toleranceSeconds > 0) {
            long skew = Math.abs(Instant.now().getEpochSecond() - timestampSeconds);
            if (skew > toleranceSeconds) {
                throw new LlamaGenException("LlamaGen webhook timestamp is outside tolerance");
            }
        }

        String expected = hmac(timestamp + "." + payload, secret);
        for (String part : signature.split(",")) {
            String candidate = part.trim();
            if (candidate.startsWith("v1=")) {
                candidate = candidate.substring(3);
            }
            if (constantTimeEquals(candidate, expected)) {
                return;
            }
        }
        throw new LlamaGenException("No matching LlamaGen webhook signature found");
    }

    private static String firstHeader(Map<String, List<String>> headers, String name) {
        if (headers == null) {
            return null;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().toLowerCase(Locale.ROOT).equals(lower)) {
                return entry.getValue() == null || entry.getValue().isEmpty() ? null : entry.getValue().get(0);
            }
        }
        return null;
    }

    private static String hmac(String value, String secret) throws LlamaGenException {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] bytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new LlamaGenException("Unable to compute LlamaGen webhook signature", e);
        }
    }

    private static boolean constantTimeEquals(String candidate, String expected) {
        byte[] candidateBytes = candidate.getBytes(StandardCharsets.UTF_8);
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        return candidateBytes.length == expectedBytes.length
                && MessageDigest.isEqual(candidateBytes, expectedBytes);
    }
}
