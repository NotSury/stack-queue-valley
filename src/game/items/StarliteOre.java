package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.FarmerStatistic;
import game.interfaces.Sellable;

/**
 * Refined celestial ore created by Astral Blessing.
 * It can be sold to the existing Merchant for coins.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class StarliteOre extends Item implements Sellable {

    private static final int SELL_PRICE = 250;

    /**
     * Creates a portable piece of Starlit Ore.
     */
    public StarliteOre() {
        super("Starlit Ore", '+');
        makePortable();
    }

    /**
     * Returns the price offered by a Merchant.
     *
     * @return price in coins
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Sells this item if the seller owns it and has a coin statistic.
     *
     * @param seller the actor selling the ore
     * @param merchant the receiving merchant
     * @param map the current map
     * @return a description of the sale
     */
    @Override
    public String sell(
            Actor seller,
            Actor merchant,
            GameMap map) {

        if (!seller.hasStatistic(FarmerStatistic.COINS)) {
            return seller
                    + " cannot sell Starlit Ore without a wallet.";
        }

        if (!seller.getInventory().getItems().contains(this)) {
            return seller
                    + " does not own this Starlit Ore.";
        }

        seller.getInventory().remove(this);

        seller.modifyStatistic(
                FarmerStatistic.COINS,
                StatisticOperations.INCREASE,
                SELL_PRICE
        );

        return seller + " sells Starlit Ore for "
                + SELL_PRICE + " coins.";
    }
}
