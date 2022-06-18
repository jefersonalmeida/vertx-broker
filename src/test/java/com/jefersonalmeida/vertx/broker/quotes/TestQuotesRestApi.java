package com.jefersonalmeida.vertx.broker.quotes;

import com.jefersonalmeida.vertx.broker.AbstractRestApiTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestQuotesRestApi extends AbstractRestApiTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);

  @Test
  void return_quote_for_asset(Vertx vertx, VertxTestContext context) {
    final var client = getClient(vertx);
    client.get("/quotes/AMZN")
      .send()
      .onComplete(context.succeeding(response -> {
        final var json = response.bodyAsJsonObject();
        LOG.info("response: {}", json);
        assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        context.completeNow();
      }));
  }

  @Test
  void return_not_found_unknown_asset(Vertx vertx, VertxTestContext context) {
    final var client = getClient(vertx);
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(context.succeeding(response -> {
        final var json = response.bodyAsJsonObject();
        LOG.info("response: {}", json);
        assertEquals(404, response.statusCode());
        assertEquals(
          "{\"message\":\"Quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}",
          json.encode()
        );
        context.completeNow();
      }));
  }
}
