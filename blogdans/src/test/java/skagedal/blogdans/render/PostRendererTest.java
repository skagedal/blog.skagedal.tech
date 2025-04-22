package skagedal.blogdans.render;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PostRendererTest {

    private final PostRenderer postRenderer = new PostRenderer();

    @Test
    void simple() {
        final var content = "<p>Hello</p>";
        final var renderedPage = postRenderer.render(content);

        assertThat(renderedPage)
            .isEqualTo("""
                <!DOCTYPE html>
                <html>
                    <p>Hello</p>
                </html>
                """);
    }
}
