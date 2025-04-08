package skagedal.blogdans.entry;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class EntryCollectorsTest {
    @Test
    void removesNullValues() {
        final var entries = Stream.of(
            new PossibleEntry("key", "value"),
            new PossibleEntry("other-key", null)
        )
            .collect(EntryCollectors.nonNullEntriesToMap());

        assertThat(entries)
            .isEqualTo(Map.of("key", "value"));
    }
}