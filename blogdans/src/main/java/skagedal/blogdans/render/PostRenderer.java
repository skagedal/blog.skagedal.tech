package skagedal.blogdans.render;

import j2html.rendering.IndentedHtml;
import j2html.tags.specialized.HtmlTag;
import j2html.tags.specialized.MetaTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import skagedal.blogdans.domain.Post;
import skagedal.blogdans.domain.Site;

import java.io.IOException;

import static j2html.TagCreator.*;

@NullMarked
public class PostRenderer {

    private final Site site;

    public PostRenderer(final Site site) {
        this.site = site;
    }

    public String render(final Post post) {
        final var htmlBuilder = IndentedHtml.inMemory();
        try {
            document().render(htmlBuilder);
            buildHtml(post).render(htmlBuilder);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to render HTML", exception);
        }
        return htmlBuilder.output().toString();
    }

    private HtmlTag buildHtml(final Post post) {
        return html(
            head(
                meta().withCharset("utf-8"),
                meta().withName("viewport").withContent("width=device-width, initial-scale=1"),
                metaDescription(post),
                link().withRel("stylesheet").withHref("/css/style.css"),
                link().withRel("canonical").withHref(site.baseUri().resolve(post.slug().toString() + "/").toString()),
                title(post.title())
            ),
            rawHtml(post.htmlContent())
        );
    }

    @Nullable private static MetaTag metaDescription(final Post post) {
        if (post.excerpt() instanceof String excerpt) {
            return meta().withName("description").withContent(excerpt);
        } else {
            return null;
        }
    }

    /*
    <!DOCTYPE html>
<html>

{% include head.html %}

<body>

{% include header.html %}

<div class="page-content">
  <div class="wrapper">
    <article class="post" itemscope itemtype="http://schema.org/BlogPosting">

      <header class="post-header">
        <h1 class="post-title" itemprop="name headline">{{ page.title }}</h1>
        <p class="post-meta"><time datetime="{{ page.date | date_to_xmlschema }}" itemprop="datePublished">{{ page.date | date: "%b %-d, %Y" }}</time>{% if page.author %} • <span itemprop="author" itemscope itemtype="http://schema.org/Person"><span itemprop="name">{{ page.author }}</span></span>{% endif %}</p>
      </header>

      <div class="post-content" itemprop="articleBody">
        {{ content }}
      </div>

    </article>
  </div>
</div>

{% include footer.html %}

</body>
</html>
     */
}
