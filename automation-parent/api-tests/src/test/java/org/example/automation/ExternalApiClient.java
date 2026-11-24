package org.example.automation;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

final class ExternalApiClient {
    private final HttpClient httpClient;
    private final URI baseUri;

    ExternalApiClient(URI baseUri, Duration timeout) {
        this.baseUri = baseUri;
        this.httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
        this.timeout = timeout;
    }

    private final Duration timeout;

    String fetchStatus() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve("/external/status"))
                .timeout(timeout).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("External API returned " + response.statusCode());
        if (!response.body().matches("\\{\\s*\"status\"\\s*:\\s*\"ok\"\\s*}")) {
            throw new IOException("Invalid external API response");
        }
        return "ok";
    }
}
