package com.llamagen.service;

import com.llamagen.ApiResource;
import com.llamagen.exception.LlamaGenException;
import com.llamagen.exception.LlamaGenInvalidRequestException;
import com.llamagen.exception.LlamaGenTimeoutException;
import com.llamagen.http.HttpMethod;
import com.llamagen.model.ComicGeneration;
import com.llamagen.model.ComicUsage;
import com.llamagen.model.FileUpload;
import com.llamagen.net.RequestOptions;
import com.llamagen.net.WaitOptions;
import com.llamagen.param.ContinueComicParams;
import com.llamagen.param.CreateComicParams;
import com.llamagen.param.GetComicOptions;
import com.llamagen.param.UpdateComicPanelParams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class ComicService {
    private final ApiResource apiResource;

    public ComicService(ApiResource apiResource) {
        this.apiResource = apiResource;
    }

    public ComicGeneration create(CreateComicParams params) throws LlamaGenException {
        return create(params, null);
    }

    public ComicGeneration create(CreateComicParams params, RequestOptions options) throws LlamaGenException {
        return apiResource.request(
                HttpMethod.POST,
                "/comics/generations",
                params.toMap(),
                null,
                ComicGeneration.class,
                options);
    }

    public ComicGeneration get(String generationId) throws LlamaGenException {
        return get(generationId, null, null);
    }

    public ComicGeneration get(String generationId, GetComicOptions getOptions) throws LlamaGenException {
        return get(generationId, getOptions, null);
    }

    public ComicGeneration get(String generationId, GetComicOptions getOptions, RequestOptions options)
            throws LlamaGenException {
        requireNonBlank(generationId, "generationId");
        Map<String, Object> query = getOptions == null ? null : getOptions.toMap();
        return apiResource.request(
                HttpMethod.GET,
                "/comics/generations/" + generationId,
                null,
                query,
                ComicGeneration.class,
                options);
    }

    public ComicGeneration continueWrite(String generationId, ContinueComicParams params)
            throws LlamaGenException {
        return continueWrite(generationId, params, null);
    }

    public ComicGeneration continueWrite(String generationId, ContinueComicParams params, RequestOptions options)
            throws LlamaGenException {
        requireNonBlank(generationId, "generationId");
        return apiResource.request(
                HttpMethod.PATCH,
                "/comics/generations/" + generationId,
                params.toMap(),
                null,
                ComicGeneration.class,
                options);
    }

    public ComicGeneration updatePanel(String generationId, UpdateComicPanelParams params)
            throws LlamaGenException {
        return updatePanel(generationId, params, null);
    }

    public ComicGeneration updatePanel(String generationId, UpdateComicPanelParams params, RequestOptions options)
            throws LlamaGenException {
        requireNonBlank(generationId, "generationId");
        return apiResource.request(
                HttpMethod.PATCH,
                "/comics/generations/" + generationId,
                params.toMap(),
                null,
                ComicGeneration.class,
                options);
    }

    public ComicUsage usage() throws LlamaGenException {
        return usage(null);
    }

    public ComicUsage usage(RequestOptions options) throws LlamaGenException {
        return apiResource.request(
                HttpMethod.GET,
                "/comics/usage",
                null,
                null,
                ComicUsage.class,
                options);
    }

    public FileUpload upload(byte[] fileBytes, String filename, String contentType) throws LlamaGenException {
        return upload(fileBytes, filename, contentType, null);
    }

    public FileUpload upload(byte[] fileBytes, String filename, String contentType, RequestOptions options)
            throws LlamaGenException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new LlamaGenInvalidRequestException("fileBytes is required");
        }
        requireNonBlank(filename, "filename");
        String boundary = "llamagen-java-" + UUID.randomUUID();
        byte[] body = buildMultipartBody(fileBytes, filename, contentType, boundary);
        return apiResource.requestBytes(
                HttpMethod.POST,
                "/comics/upload",
                body,
                "multipart/form-data; boundary=" + boundary,
                null,
                FileUpload.class,
                options);
    }

    public ComicGeneration waitForCompletion(String generationId) throws LlamaGenException {
        return waitForCompletion(generationId, WaitOptions.defaults(), null);
    }

    public ComicGeneration waitForCompletion(String generationId, WaitOptions waitOptions)
            throws LlamaGenException {
        return waitForCompletion(generationId, waitOptions, null);
    }

    public ComicGeneration waitForCompletion(
            String generationId,
            WaitOptions waitOptions,
            RequestOptions requestOptions) throws LlamaGenException {
        requireNonBlank(generationId, "generationId");
        WaitOptions resolved = waitOptions == null ? WaitOptions.defaults() : waitOptions;
        Instant deadline = Instant.now().plus(resolved.getTimeout());
        while (true) {
            ComicGeneration generation = get(generationId, null, requestOptions);
            String status = generation.getStatus() == null
                    ? ""
                    : generation.getStatus().toUpperCase(Locale.ROOT);
            if (resolved.getDoneStatuses().contains(status)) {
                return generation;
            }
            if (Instant.now().isAfter(deadline)) {
                throw new LlamaGenTimeoutException("Generation " + generationId + " did not complete before timeout");
            }
            sleep(resolved);
        }
    }

    public ComicGeneration createAndWait(CreateComicParams params) throws LlamaGenException {
        return createAndWait(params, WaitOptions.defaults(), null);
    }

    public ComicGeneration createAndWait(CreateComicParams params, WaitOptions waitOptions, RequestOptions requestOptions)
            throws LlamaGenException {
        ComicGeneration created = create(params, requestOptions);
        return waitForCompletion(created.getId(), waitOptions, requestOptions);
    }

    public ComicGeneration regeneratePanel(String generationId, UpdateComicPanelParams params)
            throws LlamaGenException {
        return updatePanel(generationId, params);
    }

    public ComicGeneration updateComicPanel(String generationId, UpdateComicPanelParams params)
            throws LlamaGenException {
        return updatePanel(generationId, params);
    }

    public ComicGeneration continueComic(String generationId, ContinueComicParams params)
            throws LlamaGenException {
        return continueWrite(generationId, params);
    }

    private static void requireNonBlank(String value, String name) throws LlamaGenInvalidRequestException {
        if (value == null || value.trim().isEmpty()) {
            throw new LlamaGenInvalidRequestException(name + " is required");
        }
    }

    private static byte[] buildMultipartBody(
            byte[] fileBytes,
            String filename,
            String contentType,
            String boundary) throws LlamaGenException {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            String type = contentType == null ? "application/octet-stream" : contentType;
            write(output, "--" + boundary + "\r\n");
            write(output, "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
            write(output, "Content-Type: " + type + "\r\n\r\n");
            output.write(fileBytes);
            write(output, "\r\n--" + boundary + "--\r\n");
            return output.toByteArray();
        } catch (IOException e) {
            throw new LlamaGenException("Unable to build multipart upload body", e);
        }
    }

    private static void write(ByteArrayOutputStream output, String value) throws IOException {
        output.write(value.getBytes(StandardCharsets.UTF_8));
    }

    private static void sleep(WaitOptions options) throws LlamaGenException {
        try {
            Thread.sleep(options.getInterval().toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlamaGenTimeoutException("Interrupted while waiting for generation", e);
        }
    }
}
