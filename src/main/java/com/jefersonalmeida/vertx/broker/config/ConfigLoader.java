package com.jefersonalmeida.vertx.broker.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfigLoader {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

  public static final String SERVER_PORT = "SERVER_PORT";
  public static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = List.of(SERVER_PORT);

  public static Future<BrokerConfig> load(Vertx vertx) {

    final var exposedKeys = new JsonArray();
    EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);

    LOG.debug("Fetch configuration for {}", exposedKeys.encode());

    final var envStore = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));

    final var propertyStore = new ConfigStoreOptions()
      .setType("sys")
      .setConfig(new JsonObject().put("cache", false));

    final var retriever = ConfigRetriever.create(
      vertx,
      new ConfigRetrieverOptions()
        .addStore(propertyStore)
        .addStore(envStore)
    );

    return retriever.getConfig().map(BrokerConfig::from);
  }
}
