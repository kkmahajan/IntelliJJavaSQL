package org.example.automation;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class WireMockExternalApiTest {
    private WireMockServer server;

    @BeforeEach void start() {
        server = new WireMockServer(options().dynamicPort());
        server.start();
    }

    @AfterEach void stop() {
        if (server != null) server.stop();
    }

    private ExternalApiClient client(Duration timeout) {
        return new ExternalApiClient(java.net.URI.create(server.baseUrl()), timeout);
    }

    @Test void successfulResponse() throws Exception {
        server.stubFor(get(urlEqualTo("/external/status"))
                .willReturn(okJson("{\"status\":\"ok\"}")));
        assertEquals("ok", client(Duration.ofSeconds(3)).fetchStatus());
        server.verify(1, getRequestedFor(urlEqualTo("/external/status")));
    }

    @Test void handlesServerError() {
        server.stubFor(get(urlEqualTo("/external/status")).willReturn(serverError()));
        IOException exception = assertThrows(IOException.class,
                () -> client(Duration.ofSeconds(3)).fetchStatus());
        assertTrue(exception.getMessage().contains("500"));
    }

    @Test void unexpectedPayload() {
        server.stubFor(get(urlEqualTo("/external/status")).willReturn(okJson("{\"status\":\"unexpected\"}")));
        IOException exception = assertThrows(IOException.class,
                () -> client(Duration.ofSeconds(3)).fetchStatus());
        assertTrue(exception.getMessage().contains("Invalid"));
    }

    @Test void delayedResponseTimesOut() {
        server.stubFor(get(urlEqualTo("/external/status"))
                .willReturn(okJson("{\"status\":\"ok\"}").withFixedDelay(1500)));
        assertThrows(HttpTimeoutException.class, () -> client(Duration.ofMillis(250)).fetchStatus());
    }
}
