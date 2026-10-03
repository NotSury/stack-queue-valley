package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public class ChargedCoating implements Coating {

    private static final int EXTRA_DAMAGE = 30;

    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map){
        target.hurt(EXTRA_DAMAGE);
        return "The charge shocks " + target + " for " + EXTRA_DAMAGE + "extra damage.";
    }

    @Override
    public String toString(){
        return "Fire Coating";
    }
}
