package skagedal.blogdans;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import skagedal.blogdans.testutil.TestApp;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import static org.assertj.core.api.Assertions.*;

class AppTest {
    @Test
    void failing() {
        assertThat(true).isFalse();
    }

    @Test
    void indexPage() throws IOException, InterruptedException {
        final var testApp = TestApp.simple();

        final var httpClient = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
            .uri(testApp.baseUri().resolve("/"))
            .build();

        final var response = httpClient.send(request, BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
            .contains("First Post Title")
            .doesNotContain("First Post Body");
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
        assertThat(response.body())
            .contains("First Post Title")
            .contains("First Post Body");
    }
}