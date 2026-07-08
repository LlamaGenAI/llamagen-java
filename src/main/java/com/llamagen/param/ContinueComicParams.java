package com.llamagen.param;

import com.llamagen.exception.LlamaGenInvalidRequestException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ContinueComicParams implements Params {
    private final String prompt;
    private final Integer fixPanelNum;
    private final Map<String, Object> pagination;
    private final List<String> images;
    private final List<Map<String, Object>> attachments;
    private final Map<String, Object> extraParams;

    private ContinueComicParams(Builder builder) throws LlamaGenInvalidRequestException {
        if (CreateComicParams.isBlank(builder.prompt)) {
            throw new LlamaGenInvalidRequestException("prompt is required");
        }
        if (builder.fixPanelNum != null && builder.pagination != null) {
            throw new LlamaGenInvalidRequestException("fixPanelNum and pagination cannot be used together");
        }
        this.prompt = builder.prompt;
        this.fixPanelNum = builder.fixPanelNum;
        this.pagination = builder.pagination;
        this.images = builder.images;
        this.attachments = builder.attachments;
        this.extraParams = builder.extraParams;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(extraParams);
        map.put("action", "continueWrite");
        CreateComicParams.putIfNotNull(map, "prompt", prompt);
        CreateComicParams.putIfNotNull(map, "fixPanelNum", fixPanelNum);
        CreateComicParams.putIfNotNull(map, "pagination", pagination);
        CreateComicParams.putIfNotEmpty(map, "images", images);
        CreateComicParams.putIfNotEmpty(map, "attachments", attachments);
        return map;
    }

    public static final class Builder extends BaseParamsBuilder<Builder> {
        private String prompt;
        private Integer fixPanelNum;
        private Map<String, Object> pagination;
        private final List<String> images = new ArrayList<>();
        private final List<Map<String, Object>> attachments = new ArrayList<>();

        public Builder setPrompt(String prompt) {
            this.prompt = prompt;
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

        public Builder addAttachment(String type, String url) {
            Map<String, Object> attachment = new LinkedHashMap<>();
            if (type != null) {
                attachment.put("type", type);
            }
            attachment.put("url", url);
            this.attachments.add(attachment);
            return this;
        }

        public ContinueComicParams build() throws LlamaGenInvalidRequestException {
            return new ContinueComicParams(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
