package skagedal.blogdans.handlers;

import org.jetbrains.annotations.NotNull;
import skagedal.blogdans.domain.Site;
import skagedal.blogdans.domain.Slug;
import skagedal.blogdans.infra.Context;
import skagedal.blogdans.infra.Handler;
import skagedal.blogdans.jekyll.JekyllSite;
import skagedal.blogdans.render.Renderer;

public class PostPageHandler implements Handler {
    private final JekyllSite jekyllSite;
    private final Renderer renderer;

    public PostPageHandler(final Site site, final JekyllSite jekyllSite) {
        this.jekyllSite = jekyllSite;
        this.renderer = new Renderer(site);
    }

    @Override
    public void handle(final @NotNull Context context) {
        context.javalin().html(render(context));
    }

    private String render(final Context context) {
        final var slug = Slug.of(context.javalin().pathParam("slug"));
        final var post = jekyllSite.readPost(slug);
        return renderer.render(post, context.user());
    }
}
