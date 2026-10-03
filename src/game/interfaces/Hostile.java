package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Hostile {

    String respondToAttack(Actor attacker, GameMap map);
}
