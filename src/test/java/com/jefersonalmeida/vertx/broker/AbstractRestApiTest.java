package com.jefersonalmeida.vertx.broker;

import com.jefersonalmeida.vertx.broker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class AbstractRestApiTest {

  protected static final int TEST_SERVER_PORT = 8889;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext context) {
    System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf(TEST_SERVER_PORT));
    vertx.deployVerticle(new MainVerticle(), context.succeeding(id -> context.completeNow()));
  }

  protected WebClient getClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
  }
}
