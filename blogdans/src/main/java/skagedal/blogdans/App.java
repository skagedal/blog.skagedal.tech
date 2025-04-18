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
import skagedal.blogdans.jekyll.JekyllSite;

import java.nio.file.Path;
import java.util.function.Consumer;

public class App {
    private static Logger log = LoggerFactory.getLogger(App.class);
    private final AppConfig appConfig;

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
        final var jekyllRoot = appConfig.jekyllRoot();
        final var port = appConfig.port();
        final var jekyllSite = new JekyllSite(jekyllRoot);
        final var indexPageHandler = new IndexPageHandler(jekyllSite);
        final var postPageHandler = new PostPageHandler(jekyllSite);

        database.runMigrations();

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
