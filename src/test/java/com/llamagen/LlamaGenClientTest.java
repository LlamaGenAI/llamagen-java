package com.llamagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.llamagen.http.HttpRequest;
import com.llamagen.http.HttpResponse;
import com.llamagen.http.HttpTransport;
import com.llamagen.model.AnimationGeneration;
import com.llamagen.model.ComicGeneration;
import com.llamagen.model.ComicUsage;
import com.llamagen.model.FileUpload;
import com.llamagen.net.RequestOptions;
import com.llamagen.param.ContinueComicParams;
import com.llamagen.param.CreateAnimationParams;
import com.llamagen.param.CreateComicParams;
import com.llamagen.param.GetComicOptions;
import com.llamagen.param.UpdateComicPanelParams;
import com.llamagen.webhook.Webhook;
import com.llamagen.webhook.WebhookEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

final class LlamaGenClientTest {
    @Test
    void createsComicGenerationWithLatestFields() throws Exception {
        MockTransport transport = new MockTransport();
        transport.enqueue(200, "{\"id\":\"gen_1\",\"status\":\"PENDING\"}");
        LlamaGenClient client = testClient(transport);

        ComicGeneration generation = client.comic().create(
                CreateComicParams.builder()
                        .setPrompt("A 4-panel comic about Leo finding a glowing key.")
                        .setStyle("manga")
                        .setPagination(2, 4)
                        .addComicRole("Leo", "https://example.com/leo.png")
                        .addComicLocation("Dreamwood Forest", "https://example.com/forest.png")
                        .addAttachment("image", "https://example.com/reference.png")
                        .setLanguage("en")
                        .setUpscale("2K")
                        .build());

        assertEquals("gen_1", generation.getId());
        HttpRequest request = transport.lastRequest();
        assertEquals("https://api.llamagen.ai/v1/comics/generations", request.getUrl());
        JsonObject body = JsonParser.parseString(new String(request.getBody(), StandardCharsets.UTF_8)).getAsJsonObject();
        assertEquals("manga", body.get("style").getAsString());
        assertEquals(2, body.getAsJsonObject("pagination").get("totalPages").getAsInt());
        assertEquals("Leo", body.getAsJsonArray("comicRoles").get(0).getAsJsonObject().get("name").getAsString());
    }

    @Test
    void getsSpecificComicPanel() throws Exception {
        MockTransport transport = new MockTransport();
        transport.enqueue(200, "{\"id\":\"gen_1\",\"status\":\"PROCESSED\"}");
        LlamaGenClient client = testClient(transport);

        client.comic().get(
                "gen_1",
                GetComicOptions.builder().setPage(1).setPanel(2).build());

        assertEquals(
                "https://api.llamagen.ai/v1/comics/generations/gen_1?page=1&panel=2",
                transport.lastRequest().getUrl());
    }

    @Test
    void continuesAndUpdatesComicPanels() throws Exception {
        MockTransport transport = new MockTransport();
        transport.enqueue(200, "{\"id\":\"gen_1\",\"status\":\"LOADING\"}");
        transport.enqueue(200, "{\"id\":\"gen_1\",\"status\":\"LOADING\"}");
        LlamaGenClient client = testClient(transport);

        client.comic().continueWrite(
                "gen_1",
                ContinueComicParams.builder()
                        .setPrompt("Continue into the hidden arcade.")
                        .setFixPanelNum(4)
                        .addAttachment("image", "https://example.com/reference.png")
                        .build());

        JsonObject continueBody = requestJson(transport.requests.get(0));
        assertEquals("continueWrite", continueBody.get("action").getAsString());
        assertEquals(4, continueBody.get("fixPanelNum").getAsInt());

        client.comic().updatePanel(
                "gen_1",
                UpdateComicPanelParams.builder()
                        .setPage(0)
                        .setPanel(2)
                        .setPanelPrompt("Make Leo look more hopeful.")
                        .build());

        JsonObject updateBody = requestJson(transport.lastRequest());
        assertEquals("regeneratePanel", updateBody.get("action").getAsString());
        assertEquals(2, updateBody.get("panel").getAsInt());
    }

    @Test
    void readsUsageAndUploadsReferenceAsset() throws Exception {
        MockTransport transport = new MockTransport();
        transport.enqueue(200, "{\"apiUsageCount\":2,\"apiMaxUsage\":10,\"credits\":4500}");
        transport.enqueue(200, "{\"code\":200,\"fileUrl\":\"https://cdn.example.com/ref.png\"}");
        LlamaGenClient client = testClient(transport);

        ComicUsage usage = client.comic().usage();
        FileUpload upload = client.comic().upload("bytes".getBytes(StandardCharsets.UTF_8), "ref.png", "image/png");

        assertEquals(2, usage.getApiUsageCount());
        assertEquals("https://cdn.example.com/ref.png", upload.getFileUrl());
        assertTrue(transport.lastRequest().getHeaders().get("Content-Type").startsWith("multipart/form-data; boundary="));
    }

    @Test
    void createsAnimationGeneration() throws Exception {
        MockTransport transport = new MockTransport();
        transport.enqueue(200, "{\"id\":\"anim_1\",\"status\":\"PENDING\"}");
        LlamaGenClient client = testClient(transport);

        AnimationGeneration generation = client.animation().create(
                CreateAnimationParams.builder()
                        .setPrompt("A fox detective walks through neon rain.")
                        .setDuration(5)
                        .setResolution("720p")
                        .setAspectRatio("16:9")
                        .setImage("https://example.com/first.png")
                        .setLastFrameImage("https://example.com/last.png")
                        .addReferenceImage("https://example.com/character.png")
                        .build());

        assertEquals("anim_1", generation.getId());
        assertEquals("https://api.llamagen.ai/v1/artworks/generations", transport.lastRequest().getUrl());
        JsonObject body = requestJson(transport.lastRequest());
        assertEquals("720p", body.getAsJsonObject("videoOptions").get("resolution").getAsString());
        assertEquals("16:9", body.getAsJsonObject("videoOptions").get("aspect_ratio").getAsString());
    }

    @Test
    void supportsPerRequestOptions() throws Exception {
        MockTransport transport = new MockTransport();
        transport.enqueue(200, "{\"id\":\"gen_1\",\"status\":\"PENDING\"}");
        LlamaGenClient client = testClient(transport);

        client.comic().create(
                CreateComicParams.builder().setPrompt("A fox detective.").build(),
                RequestOptions.builder()
                        .setApiKey("request-key")
                        .setIdempotencyKey("idem_123")
                        .putHeader("X-Test", "yes")
                        .build());

        assertEquals("Bearer request-key", transport.lastRequest().getHeaders().get("Authorization"));
        assertEquals("idem_123", transport.lastRequest().getHeaders().get("Idempotency-Key"));
        assertEquals("yes", transport.lastRequest().getHeaders().get("X-Test"));
    }

    @Test
    void verifiesWebhookSignatures() throws Exception {
        String payload = "{\"type\":\"comic.generation.completed\",\"data\":{\"id\":\"gen_1\"}}";
        String secret = "whsec_test";
        String timestamp = String.valueOf(java.time.Instant.now().getEpochSecond());
        String digest = hmac(timestamp + "." + payload, secret);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-Llama-Webhook-Id", Collections.singletonList("evt_1"));
        headers.put("X-Llama-Webhook-Timestamp", Collections.singletonList(timestamp));
        headers.put("X-Llama-Webhook-Signature", Collections.singletonList("v1=" + digest));
        headers.put("X-Llama-Webhook-Request-Id", Collections.singletonList("req_1"));

        WebhookEvent event = Webhook.constructEvent(payload, headers, secret);

        assertEquals("evt_1", event.getId());
        assertEquals("req_1", event.getRequestId());
        assertEquals("comic.generation.completed", event.getType());
        assertTrue(Webhook.verifySignature(payload, headers, secret));
    }

    private static LlamaGenClient testClient(MockTransport transport) {
        return LlamaGenClient.builder()
                .setApiKey("test-key")
                .setHttpTransport(transport)
                .build();
    }

    private static JsonObject requestJson(HttpRequest request) {
        return JsonParser.parseString(new String(request.getBody(), StandardCharsets.UTF_8)).getAsJsonObject();
    }

    private static String hmac(String value, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] bytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static final class MockTransport implements HttpTransport {
        private final Queue<HttpResponse> responses = new ArrayDeque<>();
        private final List<HttpRequest> requests = new java.util.ArrayList<>();

        void enqueue(int status, String body) {
            responses.add(new HttpResponse(status, body, Collections.emptyMap()));
        }

        @Override
        public HttpResponse send(HttpRequest request) {
            requests.add(request);
            return responses.remove();
        }

        HttpRequest lastRequest() {
            return requests.get(requests.size() - 1);
        }
    }
}
