package io.vertx.workshop.quote;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This verticle exposes a HTTP endpoint to retrieve the current / last values of the maker data (quotes).
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class RestQuoteAPIVerticle extends AbstractVerticle {

  private Map<String, JsonObject> quotes = new HashMap<>();

  @Override
  public void start() throws Exception {
    vertx.eventBus().<JsonObject>consumer(GeneratorConfigVerticle.ADDRESS)
        .handler(message -> {
          final String name = message.body().getString("name");
          quotes.put(name, message.body());
        });


    vertx.createHttpServer()
        .requestHandler(request -> {
          HttpServerResponse response = request.response()
              .putHeader("content-type", "application/json");

          @Nullable final String name = request.getParam("name");
          if (name != null) {
            if (quotes.containsKey(name)) {
              response.end(Json.encodePrettily(quotes.get(name)));
            } else {
              response.setStatusCode(404).end();
            }
          } else {
            response.end(Json.encodePrettily(quotes));
          }

        })
        .listen(config().getInteger("http.port"), ar -> {
          if (ar.succeeded()) {
            System.out.println("Server started");
          } else {
            System.out.println("Cannot start the server: " + ar.cause());
          }
        });
  }
}
