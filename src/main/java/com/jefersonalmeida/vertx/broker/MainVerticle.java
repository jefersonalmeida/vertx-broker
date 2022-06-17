package com.jefersonalmeida.vertx.broker;

import com.jefersonalmeida.vertx.broker.assets.AssetsRestApi;
import com.jefersonalmeida.vertx.broker.quotes.QuotesRestApi;
import com.jefersonalmeida.vertx.broker.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 8888;

  public static void main(String[] args) {
    final var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> LOG.error("Unhandled:", error));
    vertx.deployVerticle(new MainVerticle(), ar -> {
      if (ar.failed()) {
        LOG.error("Failed to deploy:", ar.cause());
        return;
      }
      LOG.info("Deployed {}!", MainVerticle.class.getName());
    });
  }

  @Override
  public void start(Promise<Void> startPromise) {

    final var router = Router.router(vertx);

    router.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());

    AssetsRestApi.attach(router);
    QuotesRestApi.attach(router);
    WatchListRestApi.attach(router);

    vertx.createHttpServer()
      .requestHandler(router)
      .exceptionHandler(error -> LOG.error("HTTP server error: ", error))
      .listen(PORT, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        // Ignore completed response
        return;
      }
      LOG.error("Router error:", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }
}
