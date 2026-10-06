package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.grounds.Rock;
import game.interfaces.Sellable;

import java.util.List;
import java.util.Random;

// TEMPORARY STONE ITEM for REQ 3 integration

/**
 * This represents a Stone item
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.0.0
 */
public class Stone extends Item implements Sellable
{
    /**
     * The number of coins received when selling a Stone.
     */
    private static final int SELL_PRICE = 10;

    /**
     * Constructor for Stone class.
     */
    public Stone()
    {
        super("Stone", 's');
        this.makePortable();
    }

    /**
     * This returns the selling price of the Stone.
     *
     * @return the number of coins received when the Stone is sold.
     */
    @Override
    public int getSellPrice()
    {
        return SELL_PRICE;
    }

    /**
     * This sells the Stone to the Merchant.
     *
     * @param seller the actor selling the Stone
     * @param merchant the Merchant receive the Stone
     * @param map the game map contains the seller and Merchant
     * @return a description of the result of selling the Stone
     */
    @Override
    public String sell(Actor seller, Actor merchant, GameMap map)
    {
        seller.getInventory().remove(this);

        seller.modifyStatistic(FarmerStatistic.COINS,
                StatisticOperations.INCREASE, SELL_PRICE);

        Location merchantLocation = map.locationOf(merchant);
        List<Exit> exits = merchantLocation.getExits();

        if (!exits.isEmpty())
        {
            Random exitRandom = new Random();

            int randomIndex = exitRandom.nextInt(exits.size());

            Location rockLocation = exits.get(randomIndex).getDestination();

            rockLocation.setGround(new Rock());

            return seller + " sold Stone for " + SELL_PRICE
                    + " coins and Merchant built a Rock.";
        }
        return seller + " sold Stone for " + SELL_PRICE
                + " coins.";
    }
}
