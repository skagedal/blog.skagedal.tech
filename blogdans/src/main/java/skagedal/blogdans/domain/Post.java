package skagedal.blogdans.domain;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Post(
    Slug slug,
    String title,
    @Nullable String excerpt,
    String htmlContent
) {
    public static Builder builder(final Slug slug) {
        return new Builder(slug);
    }

    public static class Builder {
        private Slug slug;
        private String title = "";
        private @Nullable String excerpt = null;
        private String htmlContent = "";

        public Builder(final Slug slug) {
            this.slug = slug;
        }

        public Builder slug(final Slug slug) {
            this.slug = slug;
            return this;
        }

        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        public Builder excerpt(final @Nullable String excerpt) {
            this.excerpt = excerpt;
            return this;
        }

        public Builder htmlContent(final String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }

        public Post build() {
            return new Post(slug, title, excerpt, htmlContent);
        }
    }
}
