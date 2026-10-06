package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A status representing electrically charged ground.
 *
 * An actor standing on the charged ground takes 20 damage each turn.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class ChargedStatus implements Status {
    private static final int SHOCK_DAMAGE = 20;

    /**
     * Damages an actor standing on the charged ground.
     *
     * @param currentEntity the entity this status is attached to.
     * @param location location of the entity.
     */
    @Override
    public void tickStatus(GameEntity currentEntity, Location location){
        if (location.containsAnActor()){
            location.getActor().hurt(SHOCK_DAMAGE);
        }
    }

    /**
     * Charged ground remains charged indefinitely.
     *
     * @return true.
     */
    @Override
    public boolean isStatusActive(){
        return true;
    }
}
