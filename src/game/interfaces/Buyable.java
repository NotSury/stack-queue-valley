package game.interfaces;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Buyable
{
    int getBuyPrice();

    String buy(Actor buyer, Actor merchant, GameMap map);
}
