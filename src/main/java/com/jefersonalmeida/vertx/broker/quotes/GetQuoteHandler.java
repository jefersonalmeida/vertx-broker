package com.jefersonalmeida.vertx.broker.quotes;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
  private final Map<String, Quote> cachedQuotes;

  public GetQuoteHandler(Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(RoutingContext context) {
    final var assetParam = context.pathParam("asset");
    LOG.debug("Asset parameter: {}", assetParam);

    final var aQuote = Optional.ofNullable(cachedQuotes.get(assetParam));
    if (aQuote.isEmpty()) {
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message", "Quote for asset " + assetParam + " not available!")
          .put("path", context.normalizedPath())
          .toBuffer()
        );
      return;
    }

    final var response = aQuote.get().toJsonObject();
    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
    context.response().end(response.toBuffer());
  }
}
