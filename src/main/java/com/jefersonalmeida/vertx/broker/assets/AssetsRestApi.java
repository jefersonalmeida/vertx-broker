package com.jefersonalmeida.vertx.broker.assets;

import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "NFLX", "TSLA", "FB", "GOOG", "MSFT");

  public static void attach(Router parent, PgPool db) {
    parent.get("/assets").handler(new GetAssetsHandler());
  }
}
