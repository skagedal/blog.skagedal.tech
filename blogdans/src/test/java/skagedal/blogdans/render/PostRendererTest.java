package skagedal.blogdans.render;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import skagedal.blogdans.domain.Post;
import skagedal.blogdans.domain.Site;
import skagedal.blogdans.domain.Slug;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({
    SnapshotExtension.class
})
class PostRendererTest {

    private final PostRenderer postRenderer = new PostRenderer(new Site(URI.create("https://blog.skagedal.tech")));

    private Expect expect;

    @Test
    void render() {
        final var post = Post.builder(Slug.of("2025-04-07-first-post"))
            .title("First post")
            .build();
        final var renderedPage = postRenderer.render(post);

        expect.toMatchSnapshot(renderedPage);
    }

    @Test
    void withDescription() {
        final var post = Post.builder(Slug.of("2025-04-07-first-post"))
            .title("First post")
            .excerpt("Excerpt from First post")
            .build();
        final var renderedPage = postRenderer.render(post);

        expect.toMatchSnapshot(renderedPage);
    }

    @Disabled
    @Test
    void simple() {
        final @Language("html") String expectedHtml = """
            <!DOCTYPE html>
            <html>
                 <head>
                      <meta charset="utf-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1">
                      <title>First post</title>
                      <meta name="description" content="Excerpt from First post">
                      <link rel="stylesheet" href="/css/main.css">
                      <link rel="canonical" href="https://blog.skagedal.tech/2025-04-07-first-post/">
                 </head>
                <body>
                    <p>Hello</p>
                </body>
            </html>
            """;
    }
}
