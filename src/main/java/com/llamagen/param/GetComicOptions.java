package com.llamagen.param;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GetComicOptions implements Params {
    private final Integer page;
    private final Integer panel;

    private GetComicOptions(Builder builder) {
        this.page = builder.page;
        this.panel = builder.panel;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (page != null) {
            map.put("page", page);
        }
        if (panel != null) {
            map.put("panel", panel);
        }
        return map;
    }

    public static final class Builder {
        private Integer page;
        private Integer panel;

        public Builder setPage(Integer page) {
            this.page = page;
            return this;
        }

        public Builder setPanel(Integer panel) {
            this.panel = panel;
            return this;
        }

        public GetComicOptions build() {
            return new GetComicOptions(this);
        }
    }
}
