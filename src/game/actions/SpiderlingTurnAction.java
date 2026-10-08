package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.Spiderling;

/**
 * Executes a Spiderling's ordinary action and advances its lifetime.
 * Removes the Spiderling after its eighth completed turn.
 *
 * @author Surya Muni Peragas
 * @version 1.0.0
 */
public class SpiderlingTurnAction extends Action {

    private final Spiderling spiderling;
    private final Action ordinaryAction;

    /**
     * Creates a turn action for a Spiderling.
     *
     * @param spiderling the Spiderling completing its turn
     * @param ordinaryAction the selected wandering or idle action
     */
    public SpiderlingTurnAction(
            Spiderling spiderling,
            Action ordinaryAction
    ) {
        this.spiderling = spiderling;
        this.ordinaryAction = ordinaryAction;
    }

    /**
     * Executes the ordinary action and handles lifetime expiry.
     *
     * @param actor the Spiderling performing this action
     * @param map the map containing the Spiderling
     * @return the ordinary result and an expiry message when applicable
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = ordinaryAction.execute(actor, map);

        if (spiderling.completeTurn()) {
            spiderling.vanish(map);
            return result + "\nThe Spiderling disappears";
        }

        return result;
    }

    /**
     * Describes the wrapped ordinary action.
     *
     * @param actor the Spiderling performing this action
     * @return the ordinary action description
     */
    @Override
    public String menuDescription(Actor actor) {
        return ordinaryAction.menuDescription(actor);
    }
}
