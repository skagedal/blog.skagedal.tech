package tech.skagedal.entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class EntryCollectorsTest {
    @Test
    void removesNullValues() {
        final var entries = Stream.of(
            new PossibleEntry("key", "value"),
            new PossibleEntry("other-key", null)
        )
            .collect(EntryCollectors.nonNullEntriesToMap());

        assertEquals(Map.of("key", "value"), entries);
    }
}