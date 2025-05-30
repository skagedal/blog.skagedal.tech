package skagedal.blogdans;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import skagedal.blogdans.cli.Cli;
import skagedal.blogdans.cli.Command;
import skagedal.blogdans.config.AppConfig;
import skagedal.blogdans.domain.Site;
import skagedal.blogdans.handlers.*;
import skagedal.blogdans.jekyll.JekyllSite;

import java.nio.file.Path;
import java.util.function.Consumer;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);
    private final AppConfig appConfig;

    private static final Site SITE = Site.skagedalTech();

    public App(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public static void main(String[] args) {
        final var appConfig = AppConfig.forEnvironment();
        final var app = new App(appConfig);
        app.parseArgsAndRun(args);
    }

    public void parseArgsAndRun(final String[] args) {
        final var cli = new Cli();
        final var command = cli.parse(args);
        switch (command) {
            case Command.Help help -> cli.printHelp(help);
            case Command.Serve serve -> serveCommand();
            case Command.Import importCommand -> importCommand();
        }
    }

    private void serveCommand() {
        MDC.put("app", "blogdans");
        final var javalin = runServer();
        log.info("blogdans is up and running on port {}", javalin.port());
    }

    private void importCommand() {
        log.info("importing");
    }

    public Javalin runServer() {
        final var database = new Database(appConfig.databaseConfig());
        final var contentRoot = appConfig.contentRoot();
        final var port = appConfig.port();
        final var jekyllSite = new JekyllSite(contentRoot, appConfig.renderedPosts());
        final var indexPageHandler = new IndexPageHandler(SITE, jekyllSite);
        final var postPageHandler = new PostPageHandler(SITE, jekyllSite);

        database.runMigrations();

        final var javalin = Javalin.create(javalinConfig(contentRoot))
            .before(new BeforeRequestHandler())
            .get("/", indexPageHandler)
            .get("/posts/{slug}", postPageHandler)
            .get("/feed.xml", new CustomPageHandler(jekyllSite, jekyllSite.getFeedPath()))
            .after(new AfterRequestHandler())
            .start(port);
        return javalin;
    }

    private static Consumer<JavalinConfig> javalinConfig(final Path contentRoot) {
        return config -> {
            config.showJavalinBanner = false;
            config.staticFiles.add(staticConfig -> {;
                staticConfig.hostedPath = "/images";
                staticConfig.directory = contentRoot.resolve("images").toString();
                staticConfig.location = Location.EXTERNAL;
            });
            config.staticFiles.add(staticConfig -> {
                staticConfig.hostedPath = "/css";
                staticConfig.directory = contentRoot.resolve("css").toString();
                staticConfig.location = Location.EXTERNAL;
            });

            // Note: if there are directories here that Javalin can't find, it will just fail silently to serve
            // any static files...

//            config.staticFiles.add(staticConfig -> {
//                staticConfig.hostedPath = "/highlightcss";
//                staticConfig.directory = "assets/highlightcss";
//                staticConfig.location = Location.EXTERNAL;
//            });
        };
    }
}
