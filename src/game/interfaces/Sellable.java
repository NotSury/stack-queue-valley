package game.interfaces;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Sellable
{
    int getSellPrice();

    String sell(Actor seller, Actor merchant, GameMap map);
}
