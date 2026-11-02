package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import java.util.ArrayList;
import java.util.List;

/**
 * A cooked dish that removes all statuses from the consuming actor,
 * including beneficial statuses.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class Omelette extends ConsumableItem {

    /**
     * Creates a fresh Omelette.
     */
    public Omelette() {
        super("Omelette", 'e');
    }

    /**
     * Removes every status currently attached to the actor.
     *
     * @param actor the actor consuming this Omelette
     * @return a description of the consumption effect
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        List<Status> statuses = new ArrayList<>(actor.statuses());

        for (Status status : statuses) {
            actor.removeStatus(status);
        }

        return actor + " consumes an Omelette and clears "
                + statuses.size() + " status effects.";
    }
}