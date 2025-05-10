package skagedal.blogdans.testutil;

import org.testcontainers.containers.PostgreSQLContainer;
import skagedal.blogdans.config.DatabaseConfig;

public class Containers {
    private static PostgreSQLContainer<?> postgresContainer;

    public static synchronized PostgreSQLContainer<?> postgresContainer() {
        if (postgresContainer == null) {
            postgresContainer = new PostgreSQLContainer<>("postgres:16")
                .withReuse(true);
            postgresContainer.start();
        }
        return postgresContainer;
    }

    public static DatabaseConfig databaseConfig() {
        final var container = postgresContainer();
        return new DatabaseConfig(
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword()
        );
    }
}
