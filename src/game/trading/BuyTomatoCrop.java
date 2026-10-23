package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.grounds.TomatoCrop;
import game.interfaces.Buyable;
import game.utils.PlantingLocations;

import java.util.List;
import java.util.Random;

/**
 * Represents the purchasing behaviour for a Tomato Crop from the Merchant
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public class BuyTomatoCrop implements Buyable
{
    private static final int PRICE = 500;
    private final Random random = new Random();

    /**
     * Returns the purchase price of a Tomato Crop.
     *
     * @return the purchase price in coins
     */
    @Override
    public int getBuyPrice() {
        return PRICE;
    }

    /**
     * Attempts to purchase and plant a Tomato Crop.
     *
     * @param buyer the actor purchasing the crop
     * @param merchant the Merchant selling the crop
     * @param map the current game map
     * @return a message description of the result of purchase
     */
    @Override
    public String buy(Actor buyer, Actor merchant, GameMap map) {

        Location merchantLocation = map.locationOf(merchant);

        List<Location> availableLocations =
                PlantingLocations.plantableTilesAround(merchantLocation, true);

        if (availableLocations.isEmpty())
        {
            return "There is no available location for a Tomato Crop. No coins were charged!";
        }

        Location cropLocation = availableLocations.get(random.nextInt(availableLocations.size()));

        cropLocation.setGround(new TomatoCrop());

        boolean free = random.nextBoolean();

        if (free)
        {
            return "Merchant planted a Tomato Crop here for free!";
        }

        buyer.modifyStatistic(FarmerStatistic.COINS, StatisticOperations.DECREASE, PRICE);

        return buyer +" bought a Tomato Crop for "+ PRICE + " coins.";
    }

    /**
     * Returns the display name of this purchasable product.
     *
     * @return the name of the product
     */
    @Override
    public String toString()
    {
        return "Tomato Crop";
    }
}
