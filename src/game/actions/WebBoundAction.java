package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Uses an actor's turn to escape a web.
 * Preserves any pending continuation of a multi-turn action.
 */
public class WebBoundAction extends Action {

    private final Action continuation;

    /**
     * Creates an action that spends one turn escaping a web.
     *
     * @param continuation the action to resume afterwards,
     * or null if no continuation exists
     */
    public WebBoundAction(Action continuation) {
        this.continuation = continuation;
    }

    /**
     * Spends the current turn escaping without moving the actor.
     *
     * @param actor the trapped actor
     * @param map the map containing actor
     * @return a description of the lost turn
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return actor + " is caught is a web and spends this turn breaking free.";
    }

    /**
     * Describes the web escape action.
     *
     * @param actor the trapped actor
     * @return the action description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " breaks free from a web";
    }

    /**
     * Returns the previously paused continuation.
     *
     * @return the action to resume, or null
     */
    @Override
    public Action getNextAction() {
        return continuation;
    }
}
