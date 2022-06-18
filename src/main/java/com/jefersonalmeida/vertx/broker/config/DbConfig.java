package com.jefersonalmeida.vertx.broker.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {
  String host = "host.docker.internal";
  Integer port = 5432;
  String database = "vertx_stock_broker";
  String user = "postgres";
  String password = "postgres";

  @Override
  public String toString() {
    return "DbConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", database='" + database + '\'' +
      ", user='" + user + '\'' +
      ", password='****'" +
      '}';
  }
}
