package com.llamagen.webhook;

import com.google.gson.JsonObject;

public final class WebhookEvent {
    private String id;
    private String type;
    private Long created;
    private JsonObject data;
    private String requestId;

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Long getCreated() {
        return created;
    }

    public JsonObject getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }

    void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
