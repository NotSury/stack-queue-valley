package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.interfaces.Buyable;
import game.items.Sword;

import java.util.Random;

/**
 * Represents the purchasing behaviour for a Sword from the Merchant
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public class BuySword implements Buyable
{
    private static final int PRICE = 3000;

    /**
     * Returns the purchase price of a Sword.
     *
     * @return the purchase price in coins
     */
    @Override
    public int getBuyPrice()
    {
        return PRICE;
    }

    /**
     * Attempts to purchase and plant a Sword.
     *
     * @param buyer the actor purchasing the Sword
     * @param merchant the Merchant selling the Sword
     * @param map the current game map
     * @return a message description of the result of purchase
     */
    @Override
    public String buy(Actor buyer, Actor merchant, GameMap map)
    {
        if (buyer.getStatistic(FarmerStatistic.COINS) < PRICE)
        {
            return buyer + " does not have enough coins to buy a Sword.";
        }

        buyer.modifyStatistic(FarmerStatistic.COINS, StatisticOperations.DECREASE, PRICE);

        Random random = new Random();

        if (random.nextBoolean())
        {
            buyer.getInventory().add(new Sword());
            return buyer + " bought a Sword for " + PRICE + " coins.";
        }

        return "Merchant took " + PRICE + " coins but gave no Sword.";
    }

    /**
     * Returns the display name of this purchasable product.
     *
     * @return the name of the product
     */
    @Override
    public String toString()
    {
        return "Sword";
    }
}
