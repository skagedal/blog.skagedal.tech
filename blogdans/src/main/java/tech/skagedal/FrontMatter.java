package tech.skagedal;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tech.skagedal.entry.EntryCollectors;
import tech.skagedal.entry.PossibleEntry;

import java.util.Map;
import java.util.stream.Stream;

@NullMarked
public record FrontMatter(
    @Nullable String layout,
    @Nullable String title,
    @Nullable String summary,
    @Nullable String date
) {
    public Stream<PossibleEntry> asPossibleEntries() {
        return Stream.of(
                new PossibleEntry("layout", layout),
                new PossibleEntry("title", title),
                new PossibleEntry("summary", summary),
                new PossibleEntry("date", date)
            );
    }

    public Map<String, Object> asMap() {
        return asPossibleEntries().collect(EntryCollectors.nonNullEntriesToMap());
    }
}
