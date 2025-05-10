package skagedal.blogdans.config;

import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

public record AppConfig(
    Path contentRoot,
    Path renderedPosts,
    int port,
    DatabaseConfig databaseConfig
) {

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
            .jekyllRoot(Path.of("../content"))
            .renderedPosts(Path.of("../rendered-posts/_site"))
            .port(8081)
            .databaseConfig(DatabaseConfig.developmentConfig())
            .build();
    }

    private static AppConfig productionConfig() {
        return builder()
            .jekyllRoot(Path.of("content"))
            .renderedPosts(Path.of("rawposts"))
            .port(9020)
            .databaseConfig(DatabaseConfig.productionConfig())
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path renderedPosts = Path.of(".");
        private Path jekyllRoot = Path.of(".");
        private int port = 0;
        private @Nullable DatabaseConfig databaseConfig;

        private Builder() {
        }

        public Builder renderedPosts(Path renderedPosts) {
            this.renderedPosts = renderedPosts;
            return this;
        }

        public Builder jekyllRoot(Path jekyllRoot) {
            this.jekyllRoot = jekyllRoot;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder databaseConfig(DatabaseConfig databaseConfig) {
            this.databaseConfig = databaseConfig;
            return this;
        }

        public AppConfig build() {
            if (databaseConfig == null) {
                throw new IllegalStateException("Database config needs to be set");
            }
            return new AppConfig(jekyllRoot, renderedPosts, port, databaseConfig);
        }
    }
}
