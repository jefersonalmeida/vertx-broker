package com.jefersonalmeida.vertx.broker.assets;

import com.jefersonalmeida.vertx.broker.AbstractRestApiTest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestAssetsRestApi extends AbstractRestApiTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);

  @Test
  void return_all_assets(Vertx vertx, VertxTestContext context) {
    final var client = getClient(vertx);
    client.get("/assets")
      .send()
      .onComplete(context.succeeding(response -> {
        final var json = response.bodyAsJsonArray();
        LOG.info("response: {}", json);
        assertEquals(
          "[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"},{\"name\":\"FB\"},{\"name\":\"GOOG\"},{\"name\":\"MSFT\"}]",
          json.encode()
        );
        assertEquals(200, response.statusCode());
        assertEquals(
          HttpHeaderValues.APPLICATION_JSON.toString(),
          response.getHeader(HttpHeaders.CONTENT_TYPE.toString())
        );
        assertEquals("my-value", response.getHeader("my-header"));
        context.completeNow();
      }));
  }
}
