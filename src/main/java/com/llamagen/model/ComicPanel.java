package com.llamagen.model;

import com.google.gson.JsonObject;

public final class ComicPanel {
    private String assetUrl;
    private String image;
    private String caption;
    private Integer page;
    private Integer panel;
    private JsonObject metadata;

    public String getAssetUrl() {
        return assetUrl;
    }

    public String getImage() {
        return image;
    }

    public String getCaption() {
        return caption;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPanel() {
        return panel;
    }

    public JsonObject getMetadata() {
        return metadata;
    }
}
