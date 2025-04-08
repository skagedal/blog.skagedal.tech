package skagedal.blogdans;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import skagedal.blogdans.jekyll.JekyllSite;

public class IndexPageHandler implements Handler {
    private final JekyllSite jekyllSite;

    public IndexPageHandler(final JekyllSite jekyllSite) {
        this.jekyllSite = jekyllSite;
    }

    @Override
    public void handle(Context context) throws Exception {
        context.html(renderHtml());
    }

    @NotNull
    private String renderHtml() {
        final var path = jekyllSite.indexPath();
        return jekyllSite.render(path);
    }
}
