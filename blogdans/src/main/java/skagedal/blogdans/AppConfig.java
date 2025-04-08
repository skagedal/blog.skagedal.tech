package skagedal.blogdans;

import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public record AppConfig(Path jekyllRoot, int port) {

    public static AppConfig forEnvironment() {
        return switch (System.getenv("ENVIRONMENT")) {
            case "production", "prod" -> productionConfig();
            case "development", "dev" -> developmentConfig();
            case null ->
                throw new IllegalArgumentException("Please set the ENVIRONMENT environment variable (for example to dev)");
            default -> throw new IllegalArgumentException("Unknown environment: " + System.getenv("ENVIRONMENT"));
        };
    }

    private static AppConfig developmentConfig() {
        return builder()
            .jekyllRoot(Path.of("../jekyll"))
            .port(8081)
            .build();
    }

    private static AppConfig productionConfig() {
        return builder()
            .jekyllRoot(Path.of("content"))
            .port(9020)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path jekyllRoot = Path.of(".");
        private int port = 0;

        private Builder() {
        }

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
