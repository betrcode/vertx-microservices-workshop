package io.vertx.workshop.trader.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.workshop.portfolio.Portfolio;
import io.vertx.workshop.portfolio.PortfolioService;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A small utility class to initialize the compulsive traders and implement the stupid trading logic.
 */
public class TraderUtils {

  private final static Random RANDOM = new Random();

  public static String pickACompany() {
    int choice = RANDOM.nextInt(2);
    switch (choice) {
      case 0:
        return "Divinator";
      case 1:
        return "MacroHard";
      default:
        return "Black Coat";
    }
  }

  public static boolean timeToSell() {
    return RANDOM.nextBoolean();
  }

  public static int pickANumber() {
    return RANDOM.nextInt(6) + 1;
  }

  public static void dumbTradingLogic(String company, int numberOfShares, PortfolioService portfolio, JsonObject quote) {
    if (quote.getString("name").equals(company)) {
      if (TraderUtils.timeToSell()) {
        portfolio.sell(numberOfShares, quote, p -> {
          if (p.succeeded()) {
            System.out.println("Sold " + numberOfShares + " of " + company + "!");
          } else {
            System.out.println("D'oh, failed to sell " + numberOfShares + " of " + company + " : " + p.cause());
          }
        });
      } else {
        portfolio.buy(numberOfShares, quote, p -> {
          if (p.succeeded()) {
            System.out.println("Bought " + numberOfShares + " of " + company + " !");
          } else {
            System.out.println("D'oh, failed to buy " + numberOfShares + " of " + company + " : " + p.cause());
          }
        });
      }
    }
  }

  public static void smarterTradingLogic(String company, int numberOfShares, PortfolioService portfolio, JsonObject quote) {
    // Decide if we should buy or sell for this company

    AtomicReference<Portfolio> actualPortfolio = new AtomicReference<>();
    // Get the portfolio
    portfolio.getPortfolio(ar -> {
      if (ar.succeeded()) {
        actualPortfolio.set(ar.result());
      }
    });

    // Exploit error in pricing
    final boolean pricingError = quote.getInteger("bid") > quote.getInteger("ask");

    if (pricingError) {
      portfolio.buy(numberOfShares, quote, p -> {
        if (p.succeeded()) {
          System.out.println("Exploited pricing and bought " + numberOfShares + " of " + company + " !");
        } else {
          System.out.println("Tried to exploit price but failed to buy " + numberOfShares + " of " + company + " : " + p.cause());
        }
      });
    } else if (TraderUtils.timeToSell()) {
      portfolio.sell(numberOfShares, quote, p -> {
        if (p.succeeded()) {
          System.out.println("Randomly sold " + numberOfShares + " of " + company + "!");
        } else {
          System.out.println("D'oh, failed to sell " + numberOfShares + " of " + company + " : " + p.cause());
        }
      });
    } else {
      System.out.println("Not a good time to trade.");
      // do nothing
    }


  }

  /**
   * Version called from Groovy where json are maps.
   *
   * @param company        the company
   * @param numberOfShares the number of share to buy or sell
   * @param portfolio      the portfolio service
   * @param quote          the quote
   */
  public static void dumbTradingLogic(String company, int numberOfShares,
                                      PortfolioService portfolio, Map<String, Object> quote) {
    JsonObject json = new JsonObject(quote);
    dumbTradingLogic(company, numberOfShares, portfolio, json);
  }
}
