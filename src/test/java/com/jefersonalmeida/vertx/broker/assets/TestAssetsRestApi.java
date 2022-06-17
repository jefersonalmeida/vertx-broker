package com.jefersonalmeida.vertx.broker.assets;

import com.jefersonalmeida.vertx.broker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext context) {
    vertx.deployVerticle(new MainVerticle(), context.succeeding(id -> context.completeNow()));
  }

  @Test
  void return_all_assets(Vertx vertx, VertxTestContext context) {
    final var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/assets")
      .send()
      .onComplete(context.succeeding(response -> {
        final var json = response.bodyAsJsonArray();
        LOG.info("response: {}", json);
        assertEquals(
          "[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"},{\"name\":\"FB\"},{\"name\":\"GOOG\"},{\"name\":\"MSFT\"}]",
          json.encode()
        );
        assertEquals(200, response.statusCode());
        context.completeNow();
      }));
  }
}
