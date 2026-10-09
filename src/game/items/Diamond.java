package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.interfaces.Sellable;

import java.util.List;
import java.util.Random;


/**
 * This represents a Diamond item
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.0.0
 */
public class Diamond extends Item implements Sellable {
    private static final int SELL_PRICE = 5000;
    private static final double STEAL_CHANCE = 0.5;

    /**
     * Constructor for Diamond class.
     *
     * With the display character 'D'
     */
    public Diamond() {
        super("Diamond", 'D');
        this.makePortable();
    }

    /**
     * This returns the selling price of the Diamond.
     *
     * @return the number of coins received when the Diamond is sold.
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Sells the Diamond for coins. After the sale, the Merchant has a chance to remove
     * another random item from the seller's inventory.
     *
     * @param seller the actor selling the Diamond.
     * @param merchant the Merchant involved in the transaction.
     * @param map the map where the transaction occurs.
     * @return a message describing the sale and any stolen item.
     */
    @Override
    public String sell(Actor seller, Actor merchant, GameMap map) {
        seller.getInventory().remove(this);

        seller.modifyStatistic(FarmerStatistic.COINS,
                StatisticOperations.INCREASE, SELL_PRICE);

        if (Math.random() > STEAL_CHANCE) {
            List<Item> items = seller.getInventory().getItems();

            if (!items.isEmpty()) {
                Random itemRandom = new Random();

                int randomIndex = itemRandom.nextInt(items.size());

                Item stolenItem = items.get(randomIndex);

                seller.getInventory().remove(stolenItem);

                return seller + " sold Diamond for "
                        + SELL_PRICE + " coins, but Merchant stole "
                        + stolenItem + ".";
            }
        }
        return seller + " sold Diamond for " + SELL_PRICE + " coins.";
    }
}

