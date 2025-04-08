package skagedal.blogdans;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jspecify.annotations.NullMarked;
import skagedal.blogdans.jekyll.JekyllSite;

@NullMarked
public class PostPageHandler implements Handler {
    private final JekyllSite jekyllSite;

    public PostPageHandler(final JekyllSite jekyllSite) {
        this.jekyllSite = jekyllSite;
    }

    @Override
    public void handle(final Context context) throws Exception {
        context.html(render(context));
    }

    private String render(final Context context) {
        final var slug = context.pathParam("slug");
        final var date = jekyllSite.dateFromSlug(slug);
        return jekyllSite.render(jekyllSite.postPath(slug), date);
    }
}
