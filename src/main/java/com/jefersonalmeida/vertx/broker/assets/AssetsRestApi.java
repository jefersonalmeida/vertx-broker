package com.jefersonalmeida.vertx.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  public static void attach(Router parent) {
    parent.get("/assets").handler(context -> {
      final var response = new JsonArray();
      response
        .add(new JsonObject().put("synbol", "AAPL"))
        .add(new JsonObject().put("synbol", "AMZN"))
        .add(new JsonObject().put("synbol", "NFLX"))
        .add(new JsonObject().put("synbol", "TSLA"));

      LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());

      context.response().end(response.toBuffer());

    });
  }
}
