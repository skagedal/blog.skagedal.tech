package skagedal.blogdans.handlers;

import skagedal.blogdans.domain.Site;
import skagedal.blogdans.infra.Context;
import skagedal.blogdans.infra.Handler;

import skagedal.blogdans.jekyll.JekyllSite;
import skagedal.blogdans.render.Renderer;

import java.util.Objects;

public class IndexPageHandler implements Handler {
    private final JekyllSite jekyllSite;
    private final Renderer renderer;

    public IndexPageHandler(final Site site, final JekyllSite jekyllSite) {
        this.jekyllSite = jekyllSite;
        this.renderer = new Renderer(site);
    }

    @Override
    public void handle(Context context)  {
        context.javalin().html(renderHtml(context));
    }

    private String renderHtml(final Context context) {
        if (Objects.equals(context.javalin().queryParam("version"), "next")) {
            final var siteContext = jekyllSite.getSiteContext();
            return renderer.renderNextVersionIndexPage(context.user(), siteContext);
        } else {
            final var path = jekyllSite.indexPath();
            return jekyllSite.render(path);
        }
    }
}
