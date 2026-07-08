package com.llamagen.net;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class WaitOptions {
    private final Duration interval;
    private final Duration timeout;
    private final Set<String> doneStatuses;

    private WaitOptions(Builder builder) {
        this.interval = builder.interval;
        this.timeout = builder.timeout;
        this.doneStatuses = Collections.unmodifiableSet(new LinkedHashSet<>(builder.doneStatuses));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static WaitOptions defaults() {
        return builder().build();
    }

    public Duration getInterval() {
        return interval;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public Set<String> getDoneStatuses() {
        return doneStatuses;
    }

    public static final class Builder {
        private Duration interval = Duration.ofSeconds(5);
        private Duration timeout = Duration.ofMinutes(3);
        private final Set<String> doneStatuses = new LinkedHashSet<>(
                Arrays.asList("SUCCEEDED", "FAILED", "PROCESSED", "COMPLETED", "CANCELLED"));

        public Builder setInterval(Duration interval) {
            this.interval = interval;
            return this;
        }

        public Builder setTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setDoneStatuses(Set<String> doneStatuses) {
            this.doneStatuses.clear();
            this.doneStatuses.addAll(doneStatuses);
            return this;
        }

        public WaitOptions build() {
            return new WaitOptions(this);
        }
    }
}
