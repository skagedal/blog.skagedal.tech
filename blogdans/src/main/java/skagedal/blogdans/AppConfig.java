package skagedal.blogdans;

import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public record AppConfig(Path jekyllRoot, int port) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path jekyllRoot = Path.of(".");
        private int port = 0;

        private Builder() {}

        public Builder jekyllRoot(Path jekyllRoot) {
            this.jekyllRoot = jekyllRoot;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public AppConfig build() {
            return new AppConfig(jekyllRoot, port);
        }
    }
}
