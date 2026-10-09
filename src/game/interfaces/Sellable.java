package game.interfaces;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Represents an object that can be sold to a merchant.
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public interface Sellable
{
    /**
     * Returns the number of coins normally awarded for selling this object.
     *
     * @return the selling price in coins.
     */
    int getSellPrice();

    /**
     * Performs the product-specific selling behaviour.
     *
     * @param seller the actor selling the object.
     * @param merchant the Merchant involved in the transaction.
     * @param map the map where the transaction occurs.
     * @return a message describing the result of the sale.
     */
    String sell(Actor seller, Actor merchant, GameMap map);
}
