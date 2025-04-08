package skagedal.blogdans.testutil;

import io.javalin.Javalin;
import skagedal.blogdans.App;
import skagedal.blogdans.AppConfig;

import java.net.URI;
import java.nio.file.Paths;

public record TestApp(
    AppConfig config,
    App app,
    Javalin javalin
) {
    private static TestApp SIMPLE_INSTANCE;

    public static synchronized TestApp simple() {
        if (SIMPLE_INSTANCE == null) {
            final var simpleSite = Paths.get("src", "test", "resources", "sites", "simple");

            final var config = AppConfig.builder()
                .jekyllRoot(simpleSite)
                .build();
            final var app = new App(config);
            final var javalin = app.run();

            SIMPLE_INSTANCE = new TestApp(config, app, javalin);
        }
        return SIMPLE_INSTANCE;
    }

    public URI baseUri() {
        return URI.create("http://localhost:" + javalin.port());
    }
}
