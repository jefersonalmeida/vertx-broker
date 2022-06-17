package com.jefersonalmeida.vertx.broker.watchlist;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.UUID;

public class PutWatchListHandler implements Handler<RoutingContext> {
  private final HashMap<UUID, WatchList> watchListPerAccount;

  public PutWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    final var accountId = WatchListRestApi.getAccountId(context);

    final var json = context.body().asJsonObject();
    final var watchList = json.mapTo(WatchList.class);
    watchListPerAccount.put(UUID.fromString(accountId), watchList);

    context.response().end(json.toBuffer());
  }
}
