package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Chicken;
import game.capabilities.FarmerStatistic;
import game.interfaces.Sellable;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Egg represents an item that can be produced by a Chicken after feeding.
 *
 * <p>An Egg is a portable item that can be carried in an actor's
 * inventory or dropped onto the game map.</p>
 *
 * @author Lee Le Si <34599223>
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.1.0
 */
public class Egg extends Item implements Sellable
{
    private static final int SELL_PRICE = 40;
    private static final double CHICKEN_CHANCE = 0.5;

    /**
     * Constructor for the Egg class.
     *
     * <p>Creates a portable Egg with the display character '0'.</p>
     */
    public Egg() {
        super("Egg",'0');
        this.makePortable();
    }

    /**
     * Returns the selling price of Egg.
     *
     * @return the selling price in coins
     */
    @Override
    public int getSellPrice()
    {
        return SELL_PRICE;
    }

    /**
     * Sells the Egg to the Merchant.
     *
     * @param seller the actor selling the Egg
     * @param merchant the Merchant buying the Egg
     * @param map the current game map
     * @return a message describing the result of the sale
     */
    @Override
    public String sell(Actor seller, Actor merchant, GameMap map) {
        seller.getInventory().remove(this);

        seller.modifyStatistic(FarmerStatistic.COINS,
                StatisticOperations.INCREASE, SELL_PRICE);

        if (Math.random() > CHICKEN_CHANCE) {
            Location merchantLocation = map.locationOf(merchant);

            List<Location> availableLocations = new ArrayList<>();

            for (Exit exit : merchantLocation.getExits()) {
                Location location = exit.getDestination();

                if (!location.containsAnActor()) {
                    availableLocations.add(location);
                }
            }

            if (!availableLocations.isEmpty()) {
                Random locationRandom = new Random();

                int randomIndex =
                        locationRandom.nextInt(availableLocations.size());

                Location chickenLocation = availableLocations.get(randomIndex);

                try {
                    chickenLocation.addActor(new Chicken());

                    return seller + " sold Egg for " + SELL_PRICE +
                            " coins and a Chicken appeared.";
                } catch (GameEngineException e) {
                }
            }
        }
        return seller + " sold Egg for " + SELL_PRICE + " coins.";
    }
}