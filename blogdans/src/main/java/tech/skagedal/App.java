package tech.skagedal;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;

import java.nio.file.Path;
import java.util.function.Consumer;

public class App {
    public static void main(String[] args) {
        final var jekyllRoot = Path.of("../jekyll");
        final var jekyllSite = new JekyllSite(jekyllRoot);
        final var indexPageHandler = new IndexPageHandler(jekyllSite);
        final var postPageHandler = new PostPageHandler(jekyllSite);

        Javalin.create(javalinConfig(jekyllRoot))
            .get("/", indexPageHandler)
            .get("/posts/{slug}", postPageHandler)
            .start(8081);
    }

    private static Consumer<JavalinConfig> javalinConfig(final Path jekyllRoot) {
        return config -> config.staticFiles.add(staticConfig -> {
            staticConfig.hostedPath = "/css";
            staticConfig.directory = jekyllRoot.resolve("_site").resolve("css").toString();
            staticConfig.location = Location.EXTERNAL;
        });
    }
}
