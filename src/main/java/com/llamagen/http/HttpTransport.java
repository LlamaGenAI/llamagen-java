package com.llamagen.http;

import java.io.IOException;

public interface HttpTransport {
    HttpResponse send(HttpRequest request) throws IOException, InterruptedException;
}
