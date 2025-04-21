package skagedal.blogdans.markdown;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MarkdownRendererTest {
    MarkdownRenderer renderer = new MarkdownRenderer();

    @Test
    void basicMarkdown() {
        final var markdown = """
                # Hello world
                This is a test of the markdown renderer.
                """;

        final var html = renderer.render(markdown);

        assertThat(html).isEqualTo("""
                <h1>Hello world</h1>
                <p>This is a test of the markdown renderer.</p>
                """);
    }

    @Test
    void syntaxHighlighting() {
        final var markdown = """
                ```java
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, world!");
                    }
                }
                ```
                """;

        final var html = renderer.render(markdown);
        assertThat(html).isEqualTo("""
           <pre><code class="language-java">public class HelloWorld {
               public static void main(String[] args) {
                   System.out.println(&quot;Hello, world!&quot;);
               }
           }
           </code></pre>
           """);
    }
}
