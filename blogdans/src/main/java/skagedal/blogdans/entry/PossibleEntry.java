package skagedal.blogdans.entry;

import org.jspecify.annotations.Nullable;

public record PossibleEntry(String key, @Nullable Object value) {
}
