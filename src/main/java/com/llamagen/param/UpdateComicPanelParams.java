package com.llamagen.param;

import com.llamagen.exception.LlamaGenInvalidRequestException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class UpdateComicPanelParams implements Params {
    private final Integer page;
    private final Integer panel;
    private final String panelPrompt;
    private final String prompt;
    private final List<String> images;
    private final String caption;
    private final Map<String, Object> extraParams;

    private UpdateComicPanelParams(Builder builder) throws LlamaGenInvalidRequestException {
        if (builder.panel == null) {
            throw new LlamaGenInvalidRequestException("panel is required");
        }
        boolean hasPayload =
                !CreateComicParams.isBlank(builder.panelPrompt)
                        || !CreateComicParams.isBlank(builder.prompt)
                        || !CreateComicParams.isBlank(builder.caption)
                        || !builder.images.isEmpty();
        if (!hasPayload) {
            throw new LlamaGenInvalidRequestException("panelPrompt, prompt, images, or caption is required");
        }
        this.page = builder.page;
        this.panel = builder.panel;
        this.panelPrompt = builder.panelPrompt;
        this.prompt = builder.prompt;
        this.images = builder.images;
        this.caption = builder.caption;
        this.extraParams = builder.extraParams;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(extraParams);
        map.put("action", "regeneratePanel");
        CreateComicParams.putIfNotNull(map, "page", page);
        CreateComicParams.putIfNotNull(map, "panel", panel);
        CreateComicParams.putIfNotNull(map, "panelPrompt", panelPrompt);
        CreateComicParams.putIfNotNull(map, "prompt", prompt);
        CreateComicParams.putIfNotEmpty(map, "images", images);
        CreateComicParams.putIfNotNull(map, "caption", caption);
        return map;
    }

    public static final class Builder extends BaseParamsBuilder<Builder> {
        private Integer page;
        private Integer panel;
        private String panelPrompt;
        private String prompt;
        private final List<String> images = new ArrayList<>();
        private String caption;

        public Builder setPage(Integer page) {
            this.page = page;
            return this;
        }

        public Builder setPanel(Integer panel) {
            this.panel = panel;
            return this;
        }

        public Builder setPanelPrompt(String panelPrompt) {
            this.panelPrompt = panelPrompt;
            return this;
        }

        public Builder setPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder addImage(String imageUrl) {
            this.images.add(imageUrl);
            return this;
        }

        public Builder setCaption(String caption) {
            this.caption = caption;
            return this;
        }

        public UpdateComicPanelParams build() throws LlamaGenInvalidRequestException {
            return new UpdateComicPanelParams(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
