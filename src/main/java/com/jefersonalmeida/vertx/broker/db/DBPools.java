package com.jefersonalmeida.vertx.broker.db;

import com.jefersonalmeida.vertx.broker.config.BrokerConfig;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class DBPools {

  public static Pool createPgPool(final Vertx vertx, final BrokerConfig configuration) {
    final var connectOptions = new PgConnectOptions()
      .setHost(configuration.getDbConfig().getHost())
      .setPort(configuration.getDbConfig().getPort())
      .setDatabase(configuration.getDbConfig().getDatabase())
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword());

    final var poolOptions = new PoolOptions().setMaxSize(4);
    return PgPool.pool(vertx, connectOptions, poolOptions);
  }
}
