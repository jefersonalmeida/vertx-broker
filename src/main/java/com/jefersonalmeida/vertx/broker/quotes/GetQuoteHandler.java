package com.jefersonalmeida.vertx.broker.quotes;

import com.jefersonalmeida.vertx.broker.db.DBResponse;
import io.vertx.core.Handler;
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
      DBResponse.notFound(context, "Quote for asset " + assetParam + " not available!");
      return;
    }

    final var response = aQuote.get().toJsonObject();
    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
    context.response().end(response.toBuffer());
  }
}
