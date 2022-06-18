package com.jefersonalmeida.vertx.broker.watchlist;

import com.jefersonalmeida.vertx.broker.db.DBResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetWatchListFromDatabaseHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetWatchListFromDatabaseHandler.class);
  private final Pool db;

  public GetWatchListFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    final var accountId = WatchListRestApi.getAccountId(context);

    SqlTemplate.forQuery(db,
        "SELECT w.asset FROM broker.watchlist w where w.account_id=#{account_id}"
      )
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DBResponse.errorHandler(context, "Failed to fetch watchlist for accountId %s".formatted(accountId)))
      .onSuccess(assets -> {
        if (!assets.iterator().hasNext()) {
          DBResponse.notFound(context, "Watchlist for accountId %s is not available!".formatted(accountId));
          return;
        }
        final var response = new JsonArray();
        assets.forEach(response::add);

        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());

        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
