package tech.skagedal;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public record FrontMatter(@Nullable String layout, @Nullable String title, @Nullable String summary) {
    private record PossibleEntry(String key, @Nullable Object value) {
    }

    private record Entry(String key, Object value) {
    }

    public Map<String, Object> asMap() {
        // sorry for this hilarious exercise in null safety
        return Stream.of(
                new PossibleEntry("layout", layout),
                new PossibleEntry("title", title),
                new PossibleEntry("summary", summary)
            ).flatMap(entry -> entry.value() != null ? Stream.of(new Entry(entry.key(), entry.value())) : Stream.empty())
            .collect(Collectors.toMap(Entry::key, Entry::value));
    }
}
