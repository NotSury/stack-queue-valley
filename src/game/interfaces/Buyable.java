package game.interfaces;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Represents an object that can be purchased from a merchant.
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public interface Buyable
{
    /**
     * Returns the price required to purchase this object.
     *
     * @return the purchase price in coins.
     */
    int getBuyPrice();

    /**
     * Performs the product-specific purchase behaviour.
     *
     * @param buyer the actor buying the object.
     * @param merchant the Merchant involved in the transaction.
     * @param map the map where the transaction occurs.
     * @return a message describing the result of the purchase.
     */
    String buy(Actor buyer, Actor merchant, GameMap map);
}
