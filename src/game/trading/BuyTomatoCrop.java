package game.trading;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.grounds.TomatoCrop;
import game.interfaces.Buyable;
import game.interfaces.PlantableGround;

import java.util.ArrayList;
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
        if (buyer.getStatistic(FarmerStatistic.COINS) < PRICE) {
            return buyer + " does not have enough coins to buy a Tomato Crop.";
        }

        Location merchantLocation = map.locationOf(merchant);

        List<Location> availableLocations = new ArrayList<>();

        for (Exit exit : merchantLocation.getExits()) {
            Location location = exit.getDestination();

            if (location.getGroundAs(PlantableGround.class) != null) {
                availableLocations.add(location);
            }
        }

        if (availableLocations.isEmpty())
        {
            return "There is no available location for a Tomato Crop. " + PRICE + " coins were refunded!";
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
