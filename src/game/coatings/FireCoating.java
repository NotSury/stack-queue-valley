package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statuses.HealthOverTimeStatus;

public class FireCoating implements Coating{

    private static final int BURN_DAMAGE_PER_TURN = 5;
    private static final int BURN_DURATION = 3;

    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map){
        target.addStatus(new HealthOverTimeStatus(-BURN_DAMAGE_PER_TURN, BURN_DURATION));
        return target + " is set burning for " + BURN_DURATION + " turns. ";
    }

    @Override
    public String toString(){
        return "Fire Coating";
    }
}
