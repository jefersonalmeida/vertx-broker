package com.jefersonalmeida.vertx.broker.watchlist;

import com.jefersonalmeida.vertx.broker.AbstractRestApiTest;
import com.jefersonalmeida.vertx.broker.assets.Asset;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWatchListRestApi extends AbstractRestApiTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);

  @Test
  void adds_and_returns_watchlist_for_account(Vertx vertx, VertxTestContext context) {
    final var client = getClient(vertx);
    final var accountId = UUID.randomUUID();

    client.put("/account/watchlist/" + accountId)
      .sendJsonObject(body())
      .onComplete(context.succeeding(response -> {
        final var json = response.bodyAsJsonObject();
        LOG.info("response PUT: {}", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
        assertEquals(200, response.statusCode());
      }))
      .compose(next -> {
        client.get("/account/watchlist/" + accountId)
          .send()
          .onComplete(context.succeeding(response -> {
            final var json = response.bodyAsJsonObject();
            LOG.info("response GET: {}", json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
            assertEquals(200, response.statusCode());
            context.completeNow();
          }));
        return Future.succeededFuture();
      });
  }

  @Test
  void adds_and_deletes_watchlist_for_account(Vertx vertx, VertxTestContext context) {
    final var client = getClient(vertx);
    final var accountId = UUID.randomUUID();

    client.put("/account/watchlist/" + accountId)
      .sendJsonObject(body())
      .onComplete(context.succeeding(response -> {
        final var json = response.bodyAsJsonObject();
        LOG.info("response PUT: {}", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
        assertEquals(200, response.statusCode());
      }))
      .compose(next -> {
        client.delete("/account/watchlist/" + accountId)
          .send()
          .onComplete(context.succeeding(response -> {
            final var json = response.bodyAsJsonObject();
            LOG.info("response DELETE: {}", json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
            assertEquals(200, response.statusCode());
            context.completeNow();
          }));
        return Future.succeededFuture();
      });
  }

  private JsonObject body() {
    return new WatchList(
      Arrays.asList(
        new Asset("AMZN"),
        new Asset("TSLA")
      )
    ).toJsonObject();
  }
}
