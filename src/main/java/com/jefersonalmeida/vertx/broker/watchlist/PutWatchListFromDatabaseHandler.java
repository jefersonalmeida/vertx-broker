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

    watchList.getAssets().forEach(asset -> {

      final var parameters = new HashMap<String, Object>();
      parameters.put("account_id", accountId);
      parameters.put("asset", asset.getName());

      SqlTemplate.forUpdate(db,
          "INSERT INTO broker.watchlist VALUES (#{account_id}, #{asset})"
        )
        .execute(parameters)
        .onFailure(DBResponse.errorHandler(context, "Failed to insert into watchlist for accountId %s".formatted(accountId)))
        .onSuccess(result -> {
          if (!context.response().ended()) {
            context.response()
              .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
              .end();
          }
        })
      ;
    });
  }
}
