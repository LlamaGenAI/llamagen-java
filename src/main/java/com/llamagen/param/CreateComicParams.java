package com.llamagen.param;

import com.llamagen.exception.LlamaGenInvalidRequestException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CreateComicParams implements Params {
    private final String prompt;
    private final String style;
    private final String size;
    private final String preset;
    private final String promptUrl;
    private final Integer fixPanelNum;
    private final Map<String, Object> pagination;
    private final List<String> images;
    private final List<Map<String, Object>> comicRoles;
    private final List<Map<String, Object>> comicLocations;
    private final List<Map<String, Object>> attachments;
    private final String language;
    private final Object upscale;
    private final Map<String, Object> extraParams;

    private CreateComicParams(Builder builder) throws LlamaGenInvalidRequestException {
        if (isBlank(builder.prompt)) {
            throw new LlamaGenInvalidRequestException("prompt is required");
        }
        if (builder.fixPanelNum != null && builder.pagination != null) {
            throw new LlamaGenInvalidRequestException("fixPanelNum and pagination cannot be used together");
        }
        this.prompt = builder.prompt;
        this.style = builder.style;
        this.size = builder.size;
        this.preset = builder.preset;
        this.promptUrl = builder.promptUrl;
        this.fixPanelNum = builder.fixPanelNum;
        this.pagination = builder.pagination;
        this.images = builder.images;
        this.comicRoles = builder.comicRoles;
        this.comicLocations = builder.comicLocations;
        this.attachments = builder.attachments;
        this.language = builder.language;
        this.upscale = builder.upscale;
        this.extraParams = builder.extraParams;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(extraParams);
        putIfNotNull(map, "prompt", prompt);
        putIfNotNull(map, "style", style);
        putIfNotNull(map, "size", size);
        putIfNotNull(map, "preset", preset);
        putIfNotNull(map, "promptUrl", promptUrl);
        putIfNotNull(map, "fixPanelNum", fixPanelNum);
        putIfNotNull(map, "pagination", pagination);
        putIfNotEmpty(map, "images", images);
        putIfNotEmpty(map, "comicRoles", comicRoles);
        putIfNotEmpty(map, "comicLocations", comicLocations);
        putIfNotEmpty(map, "attachments", attachments);
        putIfNotNull(map, "language", language);
        putIfNotNull(map, "upscale", upscale);
        return map;
    }

    public static final class Builder extends BaseParamsBuilder<Builder> {
        private String prompt;
        private String style;
        private String size;
        private String preset;
        private String promptUrl;
        private Integer fixPanelNum;
        private Map<String, Object> pagination;
        private final List<String> images = new ArrayList<>();
        private final List<Map<String, Object>> comicRoles = new ArrayList<>();
        private final List<Map<String, Object>> comicLocations = new ArrayList<>();
        private final List<Map<String, Object>> attachments = new ArrayList<>();
        private String language;
        private Object upscale;

        public Builder setPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder setStyle(String style) {
            this.style = style;
            return this;
        }

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setPreset(String preset) {
            this.preset = preset;
            return this;
        }

        public Builder setPromptUrl(String promptUrl) {
            this.promptUrl = promptUrl;
            return this;
        }

        public Builder setFixPanelNum(Integer fixPanelNum) {
            this.fixPanelNum = fixPanelNum;
            return this;
        }

        public Builder setPagination(int totalPages, int panelsPerPage) {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("totalPages", totalPages);
            value.put("panelsPerPage", panelsPerPage);
            this.pagination = value;
            return this;
        }

        public Builder addImage(String imageUrl) {
            this.images.add(imageUrl);
            return this;
        }

        public Builder addComicRole(String name, String imageUrl) {
            Map<String, Object> role = new LinkedHashMap<>();
            role.put("name", name);
            if (imageUrl != null) {
                role.put("image", imageUrl);
            }
            this.comicRoles.add(role);
            return this;
        }

        public Builder addComicLocation(String name, String imageUrl) {
            Map<String, Object> location = new LinkedHashMap<>();
            location.put("name", name);
            if (imageUrl != null) {
                location.put("image", imageUrl);
            }
            this.comicLocations.add(location);
            return this;
        }

        public Builder addAttachment(String type, String url) {
            Map<String, Object> attachment = new LinkedHashMap<>();
            if (type != null) {
                attachment.put("type", type);
            }
            attachment.put("url", url);
            this.attachments.add(attachment);
            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder setUpscale(Object upscale) {
            this.upscale = upscale;
            return this;
        }

        public CreateComicParams build() throws LlamaGenInvalidRequestException {
            return new CreateComicParams(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    static void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    static void putIfNotEmpty(Map<String, Object> map, String key, List<?> value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
