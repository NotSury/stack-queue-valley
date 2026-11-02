package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.interfaces.Buyable;
import game.items.Sword;

import java.util.Objects;
import java.util.Random;

/**
 * Represents the purchasing behaviour for a Sword from the Merchant
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class BuySword implements Buyable
{
    private static final int PRICE = 3000;
    private final Random random;

    /**
     * Constructor for BuySword.
     */
    public BuySword(){
        this(new Random());
    }

    /**
     * Constructor with a random number generator.
     *
     * @param random random number generator.
     */
    public BuySword(Random random){
        this.random = Objects.requireNonNull(random);
    }

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
     * Attempts to purchase a Sword.
     *
     * @param buyer the actor purchasing the Sword
     * @param merchant the Merchant selling the Sword
     * @param map the current game map
     * @return a message description of the result of purchase
     */
    @Override
    public String buy(Actor buyer, Actor merchant, GameMap map)
    {
        buyer.modifyStatistic(FarmerStatistic.COINS, StatisticOperations.DECREASE, PRICE);

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
