package skagedal.blogdans.render;

import j2html.rendering.FlatHtml;
import j2html.tags.DomContent;
import j2html.tags.specialized.HtmlTag;
import j2html.tags.specialized.MetaTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import skagedal.blogdans.domain.Post;
import skagedal.blogdans.domain.Site;
import skagedal.blogdans.domain.User;

import java.io.IOException;

import static j2html.TagCreator.*;

@NullMarked
public class PostRenderer {

    private final Site site;

    public PostRenderer(final Site site) {
        this.site = site;
    }

    public String render(final Post post, final User user) {
        // IndentedHtml messes up the `<pre>` tags inside the pre-formatted content
        final var htmlBuilder = FlatHtml.inMemory();
        try {
            document().render(htmlBuilder);
            buildHtml(post, user).render(htmlBuilder);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to render HTML", exception);
        }
        return htmlBuilder.output().toString();
    }

    private HtmlTag buildHtml(final Post post, final User user) {
        return html(
            renderHead(post),
            body(
                rawHtml(site.header()),
                pageContent(post),
                rawHtml(site.footer()),
                userInfo(user)
            )
        );
    }

    private DomContent userInfo(final User user) {
        return switch (user) {
            case User.Anonymous ignored -> div();
            case User.Authenticated(String email) -> div()
                .withClasses("user-info")
                .withText("You are logged in as " + email);
        };
    }

    private DomContent renderHead(final Post post) {
        return head(
            meta().withCharset("utf-8"),
            meta().withName("viewport").withContent("width=device-width, initial-scale=1"),
            metaDescription(post),
            link().withRel("stylesheet").withHref("/css/main.css"),
            link().withRel("canonical").withHref(site.baseUri().resolve(post.slug().toString() + "/").toString()),
            title(post.title())
        );
    }

    @Nullable private static MetaTag metaDescription(final Post post) {
        if (post.excerpt() instanceof String excerpt) {
            return meta().withName("description").withContent(excerpt);
        } else {
            return null;
        }
    }

    private DomContent pageContent(final Post post) {
        return div()
            .withClasses("page-content")
            .with(
                div()
                    .withClasses("wrapper")
                    .with(
                        article()
                            .withClasses("post")
                            .attr("itemscope", "itemscope")
                            .attr("itemtype", "http://schema.org/BlogPosting")
                            .with(
                                header()
                                    .withClasses("post-header")
                                    .with(
                                        h1()
                                            .withClasses("post-title")
                                            .attr("itemprop", "name headline")
                                            .withText(post.title())
                                    )
                                    .with(
                                        p()
                                            .withClasses("post-meta")
                                            .with(
                                                time()
                                                    // TODO: this shouldn't necessarily be from the slug
                                                    .attr("datetime", post.slug().date().toString())
                                                    .attr("itemprop", "datePublished")
                                                    .withText(post.slug().date().toString())
                                            )
                                    )
                            )
                            .with(
                                div()
                                    .withClasses("post-content")
                                    .attr("itemprop", "articleBody")
                                    .with(rawHtml(post.htmlContent()))
                            )
                    )
            );
    }

}
