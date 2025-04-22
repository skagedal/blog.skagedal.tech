package skagedal.blogdans;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import skagedal.blogdans.jekyll.JekyllSite;
import skagedal.blogdans.render.PostRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PostPageHandler implements Handler {
    private final JekyllSite jekyllSite;
    private final Path renderedPostsPath;
    private final PostRenderer postRenderer = new PostRenderer();

    public PostPageHandler(final JekyllSite jekyllSite, final Path renderedPostsPath) {
        this.jekyllSite = jekyllSite;
        this.renderedPostsPath = renderedPostsPath;
    }

    @Override
    public void handle(final Context context) throws Exception {
        context.html(render(context));
    }

    private String render(final Context context) {
        final var slug = Slug.of(context.pathParam("slug"));
        final var path = renderedPostsPath.resolve(slug + ".html");
        final var content = readFile(path);
        // TODO
        return content;
//        return postRenderer.render(content);
    }

    private String readFile(final Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file: " + path, e);
        }
    }
}
