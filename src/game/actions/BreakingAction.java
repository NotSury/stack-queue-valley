package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ToolAbility;
import game.interfaces.Breakable;

/**
 * An action that allows an actor to break a Breakable target.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class BreakingAction extends Action {
    private final Breakable target;
    private final Location location;

    /**
     * Creates a breaking action for a target.
     *
     * @param target breakable target.
     * @param location location of the target.
     */
    public BreakingAction(Breakable target, Location location){
        this.target = target;
        this.location = location;
    }

    /**
     * Breaks the target if the actor has a tool with the BREAKING ability.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return result of the breaking action.
     */
    @Override
    public String execute(Actor actor, GameMap map){
        if(!actor.hasAbility(ToolAbility.BREAKING)){
            return actor + " does not have a tool that can break " + target + ".";
        }
        return target.breakBy(actor, location);
    }

    /**
     * Returns the description shown in the action menu.
     *
     * @param actor The actor performing the action.
     * @return menu description.
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " break " + target;
    }
}
