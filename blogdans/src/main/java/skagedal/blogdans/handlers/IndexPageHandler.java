package skagedal.blogdans.handlers;

import skagedal.blogdans.infra.Context;
import skagedal.blogdans.infra.Handler;

import skagedal.blogdans.jekyll.JekyllSite;

public class IndexPageHandler implements Handler {
    private final JekyllSite jekyllSite;

    public IndexPageHandler(final JekyllSite jekyllSite) {
        this.jekyllSite = jekyllSite;
    }

    @Override
    public void handle(Context context)  {
        context.javalin().html(renderHtml());
    }

    private String renderHtml() {
        final var path = jekyllSite.indexPath();
        return jekyllSite.render(path);
    }
}
