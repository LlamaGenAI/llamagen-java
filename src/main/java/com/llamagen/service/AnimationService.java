package com.llamagen.service;

import com.llamagen.ApiResource;
import com.llamagen.exception.LlamaGenException;
import com.llamagen.exception.LlamaGenInvalidRequestException;
import com.llamagen.exception.LlamaGenTimeoutException;
import com.llamagen.http.HttpMethod;
import com.llamagen.model.AnimationGeneration;
import com.llamagen.net.RequestOptions;
import com.llamagen.net.WaitOptions;
import com.llamagen.param.CreateAnimationParams;
import java.time.Instant;
import java.util.Locale;

public final class AnimationService {
    private final ApiResource apiResource;

    public AnimationService(ApiResource apiResource) {
        this.apiResource = apiResource;
    }

    public AnimationGeneration create(CreateAnimationParams params) throws LlamaGenException {
        return create(params, null);
    }

    public AnimationGeneration create(CreateAnimationParams params, RequestOptions options)
            throws LlamaGenException {
        return apiResource.request(
                HttpMethod.POST,
                "/artworks/generations",
                params.toMap(),
                null,
                AnimationGeneration.class,
                options);
    }

    public AnimationGeneration get(String artworkId) throws LlamaGenException {
        return get(artworkId, null);
    }

    public AnimationGeneration get(String artworkId, RequestOptions options) throws LlamaGenException {
        requireNonBlank(artworkId, "artworkId");
        return apiResource.request(
                HttpMethod.GET,
                "/artworks/generations/" + artworkId,
                null,
                null,
                AnimationGeneration.class,
                options);
    }

    public AnimationGeneration waitForCompletion(String artworkId) throws LlamaGenException {
        return waitForCompletion(artworkId, WaitOptions.defaults(), null);
    }

    public AnimationGeneration waitForCompletion(String artworkId, WaitOptions waitOptions)
            throws LlamaGenException {
        return waitForCompletion(artworkId, waitOptions, null);
    }

    public AnimationGeneration waitForCompletion(
            String artworkId,
            WaitOptions waitOptions,
            RequestOptions requestOptions) throws LlamaGenException {
        requireNonBlank(artworkId, "artworkId");
        WaitOptions resolved = waitOptions == null ? WaitOptions.defaults() : waitOptions;
        Instant deadline = Instant.now().plus(resolved.getTimeout());
        while (true) {
            AnimationGeneration generation = get(artworkId, requestOptions);
            String status = generation.getStatus() == null
                    ? ""
                    : generation.getStatus().toUpperCase(Locale.ROOT);
            if (resolved.getDoneStatuses().contains(status)) {
                return generation;
            }
            if (Instant.now().isAfter(deadline)) {
                throw new LlamaGenTimeoutException("Animation " + artworkId + " did not complete before timeout");
            }
            sleep(resolved);
        }
    }

    public AnimationGeneration createAndWait(CreateAnimationParams params) throws LlamaGenException {
        return createAndWait(params, WaitOptions.defaults(), null);
    }

    public AnimationGeneration createAndWait(
            CreateAnimationParams params,
            WaitOptions waitOptions,
            RequestOptions requestOptions) throws LlamaGenException {
        AnimationGeneration created = create(params, requestOptions);
        return waitForCompletion(created.getId(), waitOptions, requestOptions);
    }

    private static void requireNonBlank(String value, String name) throws LlamaGenInvalidRequestException {
        if (value == null || value.trim().isEmpty()) {
            throw new LlamaGenInvalidRequestException(name + " is required");
        }
    }

    private static void sleep(WaitOptions options) throws LlamaGenException {
        try {
            Thread.sleep(options.getInterval().toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlamaGenTimeoutException("Interrupted while waiting for animation", e);
        }
    }
}
