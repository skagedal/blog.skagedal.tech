package tech.skagedal;

import org.junit.jupiter.api.Test;
import tech.skagedal.testutil.TestApp;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import static org.assertj.core.api.Assertions.*;

class AppTest {
    @Test
    void indexPage() throws IOException, InterruptedException {
        final var testApp = TestApp.simple();

        final var httpClient = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(testApp.baseUri().resolve("/"))
            .build();

        final var response = httpClient.send(request, BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Hello world");
    }

    @Test
    void post() throws IOException, InterruptedException {
        final var testApp = TestApp.simple();

        final var httpClient = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(testApp.baseUri().resolve("/posts/2020-01-01-first-post"))
            .build();

        final var response = httpClient.send(request, BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("First post");
    }
}