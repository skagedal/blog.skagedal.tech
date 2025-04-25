package skagedal.blogdans.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import skagedal.blogdans.domain.Site;
import skagedal.blogdans.domain.Slug;
import skagedal.blogdans.jekyll.JekyllSite;
import skagedal.blogdans.render.PostRenderer;

public class PostPageHandler implements Handler {
    private final JekyllSite jekyllSite;
    private final PostRenderer postRenderer;

    public PostPageHandler(final Site site, final JekyllSite jekyllSite) {
        this.jekyllSite = jekyllSite;
        this.postRenderer = new PostRenderer(site);
    }

    @Override
    public void handle(final Context context) throws Exception {
        context.html(render(context));
    }

    private String render(final Context context) {
        final var slug = Slug.of(context.pathParam("slug"));
        final var post = jekyllSite.readPost(slug);
        return postRenderer.render(post);
    }
}
