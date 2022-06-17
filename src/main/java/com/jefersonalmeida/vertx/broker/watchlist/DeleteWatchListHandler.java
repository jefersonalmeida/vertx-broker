package com.jefersonalmeida.vertx.broker.watchlist;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(DeleteWatchListHandler.class);
  private final HashMap<UUID, WatchList> watchListPerAccount;

  public DeleteWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    final var accountId = WatchListRestApi.getAccountId(context);

    final var removed = watchListPerAccount.remove(UUID.fromString(accountId));
    LOG.debug("Deleted: {}, Remaining: {}", removed, watchListPerAccount.values());

    context.response().end(removed.toJsonObject().toBuffer());
  }
}
