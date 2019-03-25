package io.vertx.workshop.quote;

import io.vertx.core.AbstractVerticle;

import java.util.Random;

public class BullBearVerticle extends AbstractVerticle {

  private final Random random = new Random();

  @Override
  public void start() throws Exception {
    super.start();
    System.out.println("BullBear started");

    vertx.setPeriodic(1000L, loop -> {

      if (random.nextBoolean()) {
        System.out.println("BULL");
      } else {
        System.out.println("BEAR");
      }

    });
  }
}
