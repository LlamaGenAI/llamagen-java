package com.llamagen.param;

import com.llamagen.exception.LlamaGenInvalidRequestException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CreateAnimationParams implements Params {
    private final String prompt;
    private final Map<String, Object> videoOptions;
    private final Map<String, Object> extraParams;

    private CreateAnimationParams(Builder builder) throws LlamaGenInvalidRequestException {
        if (CreateComicParams.isBlank(builder.prompt)) {
            throw new LlamaGenInvalidRequestException("prompt is required");
        }
        this.prompt = builder.prompt;
        this.videoOptions = builder.videoOptions.toMap();
        this.extraParams = builder.extraParams;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(extraParams);
        map.put("prompt", prompt);
        if (!videoOptions.isEmpty()) {
            map.put("videoOptions", videoOptions);
        }
        return map;
    }

    public static final class Builder extends BaseParamsBuilder<Builder> {
        private String prompt;
        private VideoOptions.Builder videoOptions = VideoOptions.builder();

        public Builder setPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder setVideoOptions(VideoOptions videoOptions) {
            this.videoOptions = VideoOptions.builder().putAll(videoOptions.toMap());
            return this;
        }

        public Builder setDuration(Integer duration) {
            this.videoOptions.setDuration(duration);
            return this;
        }

        public Builder setResolution(String resolution) {
            this.videoOptions.setResolution(resolution);
            return this;
        }

        public Builder setAspectRatio(String aspectRatio) {
            this.videoOptions.setAspectRatio(aspectRatio);
            return this;
        }

        public Builder setImage(String image) {
            this.videoOptions.setImage(image);
            return this;
        }

        public Builder setLastFrameImage(String image) {
            this.videoOptions.setLastFrameImage(image);
            return this;
        }

        public Builder addReferenceImage(String image) {
            this.videoOptions.addReferenceImage(image);
            return this;
        }

        public CreateAnimationParams build() throws LlamaGenInvalidRequestException {
            return new CreateAnimationParams(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public static final class VideoOptions implements Params {
        private final Map<String, Object> values;

        private VideoOptions(Map<String, Object> values) {
            this.values = values;
        }

        public static Builder builder() {
            return new Builder();
        }

        @Override
        public Map<String, Object> toMap() {
            return new LinkedHashMap<>(values);
        }

        public static final class Builder {
            private final Map<String, Object> values = new LinkedHashMap<>();
            private final List<String> referenceImages = new ArrayList<>();

            public Builder setDuration(Integer duration) {
                values.put("duration", duration);
                return this;
            }

            public Builder setResolution(String resolution) {
                values.put("resolution", resolution);
                return this;
            }

            public Builder setAspectRatio(String aspectRatio) {
                values.put("aspect_ratio", aspectRatio);
                return this;
            }

            public Builder setImage(String image) {
                values.put("image", image);
                return this;
            }

            public Builder setLastFrameImage(String image) {
                values.put("last_frame_image", image);
                return this;
            }

            public Builder addReferenceImage(String image) {
                referenceImages.add(image);
                return this;
            }

            public Builder putOption(String key, Object value) {
                values.put(key, value);
                return this;
            }

            Builder putAll(Map<String, Object> map) {
                values.putAll(map);
                return this;
            }

            public VideoOptions build() {
                Map<String, Object> copy = new LinkedHashMap<>(values);
                if (!referenceImages.isEmpty()) {
                    copy.put("reference_images", new ArrayList<>(referenceImages));
                }
                return new VideoOptions(copy);
            }

            Map<String, Object> toMap() {
                return build().toMap();
            }
        }
    }
}
