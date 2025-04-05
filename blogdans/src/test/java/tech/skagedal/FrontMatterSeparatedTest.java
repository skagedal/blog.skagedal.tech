package tech.skagedal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrontMatterSeparatedTest {
    @Test
    void testFrontMatter() {
        final var input = "---\ntitle: My Title\n---\n# My Title\n";
        final var expectedFrontMatter = "title: My Title\n";
        final var expectedContent = "# My Title\n";

        final var frontMatter = FrontMatterSeparated.split(input);
        assertEquals(expectedFrontMatter, frontMatter.frontMatter());
        assertEquals(expectedContent, frontMatter.content());
    }
}