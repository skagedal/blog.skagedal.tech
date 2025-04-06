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
    public static FrontMatter empty() {
        return new FrontMatter(null, null, null, null);
    }

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String layout;
        private String title;
        private String summary;
        private String date;

        private Builder() {}

        public Builder layout(String layout) {
            this.layout = layout;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public FrontMatter build() {
            return new FrontMatter(layout, title, summary, date);
        }
    }
}
