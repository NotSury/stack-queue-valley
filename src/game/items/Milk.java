package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.interfaces.Sellable;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;

/**
 * Milk represents an item that is produced when a Cow is fed.
 *
 * <p>Milk is portable and can therefore be carried in an actor's
 * inventory or dropped onto the game map.</p>
 *
 * @author Lee Le Si <34599223>
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.1.0
 */
public class Milk extends Item implements Sellable
{
    private static final int SELL_PRICE = 60;
    private static final double PAYMENT_CHANCE = 0.5;

    /**
     * Constructor for the Milk class.
     *
     * <p>Creates a portable Milk item with the display character 'M'.</p>
     */
    public Milk() {
        super("Milk",'M');
        this.makePortable();
    }

    @Override
    public int getSellPrice()
    {
        return SELL_PRICE;
    }

    @Override
    public String sell(Actor seller, Actor merchant, GameMap map)
    {
        seller.getInventory().remove(this);

        if (Math.random() > PAYMENT_CHANCE)
        {
            seller.modifyStatistic(FarmerStatistic.COINS,
                    StatisticOperations.INCREASE, SELL_PRICE);

            return seller + " sold Milk for " + SELL_PRICE + " coins.";
        }
        return "Merchant took the Milk but paid nothing.";
    }
}
