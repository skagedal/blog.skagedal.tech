package skagedal.blogdans.config;

import com.zaxxer.hikari.HikariConfig;

public record DatabaseConfig(
    String jdbcUrl,
    String username,
    String password
) {
    public static DatabaseConfig developmentConfig() {
        return new DatabaseConfig(
            "jdbc:postgresql://localhost:5432/blogdans",
            "admin",
            "admin"
        );
    }

    public static DatabaseConfig productionConfig() {
        return new DatabaseConfig(
            "jdbc:postgresql://localhost:5432/blogdans",
            "blogdans",
            Secrets.readSecret("db-password")
        );
    }

    public HikariConfig toHikariConfig() {
        final var config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        return config;
    }
}
