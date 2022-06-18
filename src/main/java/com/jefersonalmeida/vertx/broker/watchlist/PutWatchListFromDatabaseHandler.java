package com.jefersonalmeida.vertx.broker.watchlist;

import com.jefersonalmeida.vertx.broker.db.DBResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PutWatchListFromDatabaseHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(PutWatchListFromDatabaseHandler.class);
  private final Pool db;

  public PutWatchListFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    final var accountId = WatchListRestApi.getAccountId(context);

    final var json = context.body().asJsonObject();
    final var watchList = json.mapTo(WatchList.class);


    final var parameterBatch = watchList.getAssets().stream()
      .map(asset -> {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("account_id", accountId);
        parameters.put("asset", asset.getName());
        return parameters;
      })
      .toList();


    // Only adding is possible -> Entries for watch list are never removed
    SqlTemplate.forUpdate(db,
        "INSERT INTO broker.watchlist VALUES (#{account_id}, #{asset})"
          + " ON CONFLICT (account_id, asset) DO NOTHING"
      )
      .executeBatch(parameterBatch)
      .onFailure(DBResponse.errorHandler(context, "Failed to insert into watchlist for accountId %s".formatted(accountId)))
      .onSuccess(result -> context.response()
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end()
      );

  }
}
