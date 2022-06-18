package com.jefersonalmeida.vertx.broker.quotes;

import com.jefersonalmeida.vertx.broker.assets.Asset;
import com.jefersonalmeida.vertx.broker.assets.AssetsRestApi;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  public static void attach(Router parent, Pool db) {
    final var cachedQuotes = new HashMap<String, Quote>();
    AssetsRestApi.ASSETS.forEach(symbol -> cachedQuotes.put(symbol, initRandomQuote(symbol)));
    parent.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
    parent.get("/pg/quotes/:asset").handler(new GetQuoteFromDatabaseHandler(db));
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
