package skagedal.blogdans.render;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({
    SnapshotExtension.class
})
class PostRendererTest {

    private final PostRenderer postRenderer = new PostRenderer();

    private Expect expect;

    @Test
    void render() {
        final var content = "<p>Hello</p>";
        final var renderedPage = postRenderer.render("First post", content);

        expect.toMatchSnapshot(renderedPage);
    }

    @Disabled
    @Test
    void simple() {
        final var content = "<p>Hello</p>";
        final var renderedPage = postRenderer.render("First post", content);

        final @Language("html") String expectedHtml = """
            <!DOCTYPE html>
            <html>
                 <head>
                      <meta charset="utf-8">
                      <meta http-equiv="X-UA-Compatible" content="IE=edge">
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
        assertThat(renderedPage)
            .isEqualTo(expectedHtml);
    }
}
