package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface Coating {

    String applyEffect(Actor attacker, Actor target, GameMap map);
}
