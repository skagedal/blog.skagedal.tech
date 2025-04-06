package tech.skagedal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FrontMatterSeparatedTest {
    @Test
    void testFrontMatter() {
        final var input = "---\ntitle: My Title\n---\n# My Title\n";

        final var frontMatter = FrontMatterSeparated.split(input);

        assertThat(frontMatter.frontMatter())
            .isEqualTo(FrontMatter.builder().title("My Title").build());

        assertThat(frontMatter.content())
            .isEqualTo("# My Title\n");
    }
}