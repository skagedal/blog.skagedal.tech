package tech.skagedal;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

class AppTest {
    @Test
    void startApp() throws IOException, InterruptedException {
        final var simpleSite = Paths.get("src", "test", "resources", "sites", "simple");

        final var config = AppConfig.builder()
            .jekyllRoot(simpleSite)
            .build();
        final var app = new App(config);
        final var javalin = app.run();

        final var httpClient = HttpClient.newHttpClient();
        final var request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("http://localhost:" + javalin.port() + "/"))
            .build();
        final var response = httpClient.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Hello world");

        javalin.stop();
    }
}