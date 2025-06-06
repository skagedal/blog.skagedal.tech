package skagedal.blogdans.handlers;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import skagedal.blogdans.jekyll.JekyllSite;

import java.nio.file.Path;

public class CustomPageHandler implements Handler {
    private final JekyllSite jekyllSite;
    private final Path sourcePath;

    public CustomPageHandler(final JekyllSite jekyllSite, final Path sourcePath) {
        this.jekyllSite = jekyllSite;
        this.sourcePath = sourcePath;
    }

    @Override
    public void handle(final Context context) throws Exception {
        final var rendered = jekyllSite.render(sourcePath);
        if (sourcePath.getFileName().toString().endsWith(".xml")) {
            context
                .contentType(ContentType.XML)
                .result(rendered);
        } else {
            context.contentType(ContentType.TEXT_HTML);
        }
    }
}
