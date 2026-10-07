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
    int getBuyPrice();

    String buy(Actor buyer, Actor merchant, GameMap map);
}
