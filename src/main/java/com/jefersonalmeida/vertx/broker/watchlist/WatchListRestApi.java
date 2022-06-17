package com.jefersonalmeida.vertx.broker.watchlist;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class WatchListRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public static void attach(Router parent) {

    final var watchListPerAccount = new HashMap<UUID, WatchList>();

    final var path = "/account/watchlist/:accountId";

    parent.get(path).handler(context -> {
      final var accountId = getAccountId(context);

      final var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
      if (watchList.isEmpty()) {
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "Watchlist for account " + accountId + " not available!")
            .put("path", context.normalizedPath())
            .toBuffer()
          );
        return;
      }

      final var response = watchList.get().toJsonObject();
      LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
      context.response().end(response.toBuffer());

    });

    parent.put(path).handler(context -> {
      final var accountId = getAccountId(context);

      final var json = context.body().asJsonObject();
      final var watchList = json.mapTo(WatchList.class);
      watchListPerAccount.put(UUID.fromString(accountId), watchList);

      context.response().end(json.toBuffer());
    });

    parent.delete(path).handler(context -> {
      final var accountId = getAccountId(context);

      final var removed = watchListPerAccount.remove(UUID.fromString(accountId));
      LOG.debug("Deleted: {}, Remaining: {}", removed, watchListPerAccount.values());

      context.response().end(removed.toJsonObject().toBuffer());
    });
  }

  private static String getAccountId(RoutingContext context) {
    final var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    return accountId;
  }
}
