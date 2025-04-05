package tech.skagedal;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class IndexPage implements Handler {
    private final JekyllSite jekyllSite;

    public IndexPage(final JekyllSite jekyllSite) {
        this.jekyllSite = jekyllSite;
    }

    @Override
    public void handle(Context context) throws Exception {
        context.html(renderHtml());
    }

    @NotNull
    private String renderHtml() {
        final var path = jekyllSite.indexPath();
        return jekyllSite.renderHtml(path);
    }
}
