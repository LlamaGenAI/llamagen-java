package com.llamagen.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public final class JavaNetHttpTransport implements HttpTransport {
    private final Duration defaultConnectTimeout;

    public JavaNetHttpTransport(Duration connectTimeout) {
        this.defaultConnectTimeout = connectTimeout;
    }

    @Override
    public HttpResponse send(HttpRequest request) throws IOException, InterruptedException {
        HttpURLConnection connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
        connection.setRequestMethod(request.getMethod().name());
        Duration connectTimeout = request.getConnectTimeout() == null ? defaultConnectTimeout : request.getConnectTimeout();
        if (connectTimeout != null) {
            connection.setConnectTimeout(toMillis(connectTimeout));
        }
        if (request.getReadTimeout() != null) {
            connection.setReadTimeout(toMillis(request.getReadTimeout()));
        }
        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        if (request.getBody() != null) {
            connection.setDoOutput(true);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(request.getBody());
            }
        }

        int statusCode = connection.getResponseCode();
        String body = readBody(statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream());
        Map<String, List<String>> headers = connection.getHeaderFields();
        connection.disconnect();
        return new HttpResponse(statusCode, body, headers);
    }

    private static int toMillis(Duration duration) {
        long millis = duration.toMillis();
        return millis > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) millis;
    }

    private static String readBody(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        try (InputStream input = stream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
