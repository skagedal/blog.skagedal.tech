package skagedal.blogdans.jekyll;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class YamlerTest {

    @Test
    void load() {
        final var input = "---\ntitle: My Title\n---\n# My Title\n";

        final var yamler = new Yamler();
        final var frontMatter = yamler.load(input);
        assertThat(frontMatter)
            .isEqualTo(FrontMatter.builder().title("My Title").build());
    }
}
