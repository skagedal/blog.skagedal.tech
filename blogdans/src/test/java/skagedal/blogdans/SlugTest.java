package skagedal.blogdans;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import skagedal.blogdans.domain.Slug;

import static org.assertj.core.api.Assertions.*;

class SlugTest {
    @Test
    void valid() {
        final var slug = Slug.of("2023-10-01-my-title");

        assertThat(slug.date())
            .isEqualTo("2023-10-01");
        assertThat(slug.title())
            .isEqualTo("my-title");
        assertThat(slug.toString())
            .isEqualTo("2023-10-01-my-title");
    }

    @ParameterizedTest
    @ValueSource(strings = { "2023-10-01", "2023-10-01-", "foo" })
    void invalid(String invalidSlug) {
        assertThatThrownBy(() -> Slug.of(invalidSlug))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid slug format");
    }
}
