package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.JumpPlanner;

/**
 * Moves an actor two tiles in one grid direction.
 *
 * <p>Revalidates the jump before moving. The middle tile is skipped while the
 * destination must remain legal.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class JumpAction extends Action {
    private final Location landing;
    private final JumpPlanner jumpPlanner;

    /**
     * Creates a jump to a selected landing.
     *
     * @param landing the proposed destination.
     * @param jumpPlanner the collaborator used to validate the jump.
     */
    public JumpAction(Location landing, JumpPlanner jumpPlanner){
        this.landing = landing;
        this.jumpPlanner = jumpPlanner;
    }

    /**
     * Executes the jump if its destination remains legal.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return a description of the jump or its failure.
     */
    @Override
    public String execute(Actor actor, GameMap map){
        if (!map.contains(actor)){
            return actor + " cannot jump because it is no longer on this map.";
        }

        Location origin = map.locationOf(actor);

        if (!jumpPlanner.isLegalJump(actor, origin, landing)){
            return actor + " cannot jump to (" + landing.x() + ", " + landing.y() + ") because the landing" +
                    "is no longer legal.";
        }

        map.moveActor(actor, landing);

        return actor + " jumps from (" + origin.x() + ", " + origin.y() + ") to (" +
                landing.x() + ", " + landing.y() + "),";
    }

    /**
     * Describes the proposed jump.
     *
     * @param actor The actor performing the action.
     * @return the jump description.
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " jumps to (" + landing.x() + ", " + landing.y() + ").";
    }
}
