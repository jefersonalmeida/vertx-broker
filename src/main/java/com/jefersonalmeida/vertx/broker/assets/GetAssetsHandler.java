package com.jefersonalmeida.vertx.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(final RoutingContext context) {

    final var response = new JsonArray();
    AssetsRestApi.ASSETS.stream().map(Asset::new).forEach(response::add);

    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("my-header", "my-value")
      .end(response.toBuffer());
  }
}
