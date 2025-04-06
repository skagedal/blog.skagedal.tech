package tech.skagedal;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import tech.skagedal.jekyll.JekyllSite;

import java.nio.file.Path;
import java.util.function.Consumer;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);
    private final AppConfig appConfig;

    public App(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public static void main(String[] args) {
        final var appConfig = AppConfig.builder()
            .jekyllRoot(Path.of("../jekyll"))
            .port(8081)
            .build();
        final var app = new App(appConfig);
        MDC.put("app", "blogdans");
        final var javalin = app.run();
        log.info("blogdans is up and running on port {}", javalin.port());
    }

    public Javalin run() {
        final var jekyllRoot = appConfig.jekyllRoot();
        final var port = appConfig.port();
        final var jekyllSite = new JekyllSite(jekyllRoot);
        final var indexPageHandler = new IndexPageHandler(jekyllSite);
        final var postPageHandler = new PostPageHandler(jekyllSite);

        final var javalin = Javalin.create(javalinConfig(jekyllRoot))
            .get("/", indexPageHandler)
            .get("/posts/{slug}", postPageHandler)
            .get("/about/", new CustomPageHandler(jekyllSite, jekyllSite.getAboutPath()))
            .get("/feed.xml", new CustomPageHandler(jekyllSite, jekyllSite.getFeedPath()))
            .start(port);
        return javalin;
    }

    private static Consumer<JavalinConfig> javalinConfig(final Path jekyllRoot) {
        return config -> {
            config.showJavalinBanner = false;
            config.staticFiles.add(staticConfig -> {;
                staticConfig.hostedPath = "/images";
                staticConfig.directory = jekyllRoot.resolve("images").toString();
                staticConfig.location = Location.EXTERNAL;
            });
            config.staticFiles.add(staticConfig -> {
                staticConfig.hostedPath = "/css";
                staticConfig.directory = jekyllRoot.resolve("_site").resolve("css").toString();
                staticConfig.location = Location.EXTERNAL;
            });
        };
    }
}
