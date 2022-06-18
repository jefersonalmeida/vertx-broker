package com.jefersonalmeida.vertx.broker.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {
  String host;
  Integer port;
  String database;
  String user;
  String password;

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
