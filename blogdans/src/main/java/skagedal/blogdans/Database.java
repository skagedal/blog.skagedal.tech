package skagedal.blogdans;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skagedal.blogdans.config.DatabaseConfig;

public class Database {
    private final HikariDataSource dataSource;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Database(DatabaseConfig config) {
        final var hikariConfig = config.toHikariConfig();
        dataSource = new HikariDataSource(hikariConfig);
    }

    public void runMigrations() {
        final var flyway = Flyway.configure().dataSource(dataSource).load();
        final var result = flyway.migrate();
        log.info("Flyway applied " + result.migrationsExecuted + " migrations");
    }
}
