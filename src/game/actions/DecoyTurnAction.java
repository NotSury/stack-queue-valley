package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.DecoyMimic;

/**
 * Executes a decoy's ordinary action and advances its lifetime.
 * Delegates movement or idling to an existing action. Removes the
 * decoy after its final turn has executed.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class DecoyTurnAction extends Action {

    private final DecoyMimic decoy;
    private final Action ordinaryAction;

    /**
     * Creates an action that completes one decoy turn.
     *
     * @param decoy the decoy performing the turn
     * @param ordinaryAction the movement or idle action selected for the decoy
     */
    public DecoyTurnAction(DecoyMimic decoy, Action ordinaryAction) {
        this.decoy = decoy;
        this.ordinaryAction = ordinaryAction;
    }

    /**
     * Executes the ordinary action and expires the decoy when required.
     *
     * @param actor the decoy performing this action
     * @param map the map containing the decoy
     * @return the ordinary action result and an expiry message when applicable
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = ordinaryAction.execute(actor, map);

        if (decoy.completeTurn()) {
            decoy.vanish(map);
            return result + "\nThe decoy Mimic fades away";
        }

        return result;
    }

    /**
     * Describes the decoy's selected ordinary action.
     *
     * @param actor the decoy performing this action
     * @return the ordinary action's menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return ordinaryAction.menuDescription(actor);
    }
}
