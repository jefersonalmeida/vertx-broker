package com.jefersonalmeida.vertx.broker.db.migration;

import com.jefersonalmeida.vertx.broker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {
  private static final Logger LOG = LoggerFactory.getLogger(FlywayMigration.class);

  public static Future<Void> migrate(Vertx vertx, DbConfig config) {
    LOG.debug("DB Config: {}", config);
    return vertx
      .<Void>executeBlocking(promise -> {
        // Flyway migration is blocking => uses JDBC
        execute(config);
        promise.complete();
      })
      .onFailure(err -> LOG.error("Failed to migrate db schema with error:", err));
  }

  private static void execute(DbConfig config) {
    final var jdbcUrl = String.format(
      "jdbc:postgresql://%s:%d/%s",
      config.getHost(),
      config.getPort(),
      config.getDatabase()
    );

    LOG.debug("Migrating DB schema using jdbc url: {}", jdbcUrl);

    final var flyway = Flyway.configure()
      .dataSource(jdbcUrl, config.getUser(), config.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();

    final var current = Optional.ofNullable(flyway.info().current());
    current.ifPresent(info -> LOG.info("db schema is at version: {}", info.getVersion()));

    final var pendingMigrations = flyway.info().pending();
    LOG.debug("Pending migrations are: {}", printMigrations(pendingMigrations));

    flyway.migrate();
  }

  private static String printMigrations(MigrationInfo[] pending) {
    if (Objects.isNull(pending)) {
      return "[]";
    }
    return Arrays.stream(pending)
      .map(each -> each.getVersion() + " - " + each.getDescription())
      .collect(Collectors.joining(",", "[", "]"));
  }
}
