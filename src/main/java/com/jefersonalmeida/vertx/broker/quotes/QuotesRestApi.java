package com.jefersonalmeida.vertx.broker.quotes;

import com.jefersonalmeida.vertx.broker.assets.Asset;
import com.jefersonalmeida.vertx.broker.assets.AssetsRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router parent) {

    final var cachedQuotes = new HashMap<String, Quote>();
    AssetsRestApi.ASSETS.forEach(symbol -> cachedQuotes.put(symbol, initRandomQuote(symbol)));

    parent.get("/quotes/:asset").handler(context -> {

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

    });
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
      .asset(new Asset(assetParam))
      .bid(randomValue())
      .ask(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
