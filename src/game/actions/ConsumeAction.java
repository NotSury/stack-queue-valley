package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.Consumable;
/**
 * ConsumeAction represents an action that allows an actor to consume
 * an object that implements the Consumable interface.
 *
 * <p>The action delegates the actual consumption behaviour to the
 * Consumable target so that different consumable objects can define
 * their own effects.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class ConsumeAction extends Action {
    /**
     * The consumable object targeted by this action.
     */
    private final Consumable target;
    /**
     * Constructor for the ConsumeAction class.
     *
     * @param target the consumable object to be consumed
     */
    public ConsumeAction(Consumable target) {
        this.target = target;
    }
    /**
     * Executes the consumption action.
     *
     * <p>The consumption behaviour is delegated to the target Consumable,
     * allowing the target to apply its own effects to the actor.</p>
     *
     * @param actor the actor consuming the target
     * @param map the game map where the action is performed
     * @return a description of the result of consumption
     */
    @Override
    public String execute(Actor actor, GameMap map){
        return target.consume(actor);
    }
    /**
     * Returns the description of this action for display in the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description of the consumption action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes " + target;
    }
}
