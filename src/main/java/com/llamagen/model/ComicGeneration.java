package com.llamagen.model;

import com.google.gson.JsonObject;
import java.util.List;

public final class ComicGeneration {
    private String id;
    private String status;
    private String prompt;
    private String output;
    private String createdAt;
    private JsonObject artwork;
    private List<ComicPage> comics;
    private List<ComicPanel> panels;
    private ComicPanel panel;
    private JsonObject pagination;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public JsonObject getArtwork() {
        return artwork;
    }

    public List<ComicPage> getComics() {
        return comics;
    }

    public List<ComicPanel> getPanels() {
        return panels;
    }

    public ComicPanel getPanel() {
        return panel;
    }

    public JsonObject getPagination() {
        return pagination;
    }

    public UsageDelta getUsage() {
        return usage;
    }

    public JsonObject getError() {
        return error;
    }
}
