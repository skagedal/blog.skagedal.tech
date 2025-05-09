package skagedal.blogdans.render;

import j2html.rendering.FlatHtml;
import j2html.tags.DomContent;
import j2html.tags.specialized.HtmlTag;
import j2html.tags.specialized.MetaTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import skagedal.blogdans.domain.MetaInfo;
import skagedal.blogdans.domain.Post;
import skagedal.blogdans.domain.Site;
import skagedal.blogdans.domain.User;

import java.io.IOException;

import static j2html.TagCreator.*;

@NullMarked
public class Renderer {

    private final Site site;

    public Renderer(final Site site) {
        this.site = site;
    }

    public String renderPost(final Post post, final User user) {
        return renderToString(buildPostHtml(post, user));
    }

    public String renderNextVersionIndexPage(final User user) {
        return renderToString(buildIndexHtml(user));
    }

    private static String renderToString(final HtmlTag htmlTag) {
        // IndentedHtml messes up the `<pre>` tags inside the pre-formatted content
        final var htmlBuilder = FlatHtml.inMemory();
        try {
            document().render(htmlBuilder);
            htmlTag.render(htmlBuilder);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to render HTML", exception);
        }
        return htmlBuilder.output().toString();
    }

    private HtmlTag buildIndexHtml(final User user) {
        final var metaInfo = new MetaInfo(site.baseUri(), "Thoughts on programming, music and other things. Feel free to e-mail me comments!", "skagedal.tech");
        return html(
            renderHead(metaInfo),
            body(
                rawHtml(site.header()),
                div(),
                rawHtml(site.footer()),
                userInfo(user)
            )
        );
    }

    private HtmlTag buildPostHtml(final Post post, final User user) {
        return html(
            renderHead(post.metaInfo(site)),
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

    private DomContent renderHead(final MetaInfo metaInfo) {
        return head(
            meta().withCharset("utf-8"),
            meta().withName("viewport").withContent("width=device-width, initial-scale=1"),
            metaDescription(metaInfo),
            link().withRel("stylesheet").withHref("/css/main.css"),
            link().withRel("canonical").withHref(metaInfo.canonicalUri().toString()),
            title(metaInfo.title())
        );
    }

    @Nullable private static MetaTag metaDescription(final MetaInfo post) {
        if (post.description() instanceof String description) {
            return meta().withName("description").withContent(description);
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
