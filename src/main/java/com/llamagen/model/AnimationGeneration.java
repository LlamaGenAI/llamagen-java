package com.llamagen.model;

import com.google.gson.JsonObject;

public final class AnimationGeneration {
    private String id;
    private String status;
    private String prompt;
    private String output;
    private String videoUrl;
    private String createdAt;
    private JsonObject artwork;
    private JsonObject videoOptions;
    private UsageDelta usage;
    private JsonObject error;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getOutput() {
        return output;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public JsonObject getArtwork() {
        return artwork;
    }

    public JsonObject getVideoOptions() {
        return videoOptions;
    }

    public UsageDelta getUsage() {
        return usage;
    }

    public JsonObject getError() {
        return error;
    }
}
