package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Consumable;

/**
 * Represents an action that allows an actor to consume a consumable object.
 */
public class ConsumeAction extends Action {

    private final Consumable consumable;

    /**
     * Constructs a new consume action.
     *
     * @param consumable the object to be consumed
     */
    public ConsumeAction(Consumable consumable) {
        this.consumable = consumable;
    }

    /**
     * Executes the consume action by delegating the consumption behavior
     * to the consumable object.
     *
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return a description of the consumption result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return consumable.consumedBy(actor);
    }

    /**
     * Returns the description of this action for the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes " + consumable;
    }
}
