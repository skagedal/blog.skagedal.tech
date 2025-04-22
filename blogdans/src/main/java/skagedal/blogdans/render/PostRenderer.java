package skagedal.blogdans.render;

import j2html.rendering.FlatHtml;
import j2html.rendering.IndentedHtml;

import java.io.IOException;

import static j2html.TagCreator.*;

public class PostRenderer {
    public String render(final String content) {
        final var htmlBuilder = IndentedHtml.inMemory();
        try {
            document().render(htmlBuilder);
            html().render(htmlBuilder);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to render HTML", exception);
        }
        return htmlBuilder.output().toString();
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
