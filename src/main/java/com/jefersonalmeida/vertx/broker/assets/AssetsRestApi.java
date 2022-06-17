package com.jefersonalmeida.vertx.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "NFLX", "TSLA", "FB", "GOOG", "MSFT");

  public static void attach(Router parent) {
    parent.get("/assets").handler(context -> {
      final var response = new JsonArray();
      ASSETS.stream().map(Asset::new).forEach(response::add);

      LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
      context.response().end(response.toBuffer());
    });
  }
}
