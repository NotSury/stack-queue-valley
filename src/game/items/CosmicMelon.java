package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import game.statuses.MelonBurstStatus;

/**
 * Cosmic Melon causes a nearby explosion on the next status tick.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CosmicMelon extends ConsumableItem {

    /**
     * Creates a portable Cosmic Melon.
     */
    public CosmicMelon() {
        super("Cosmic Melon", 'm');
    }

    /**
     * Arms a delayed 12-damage explosion.
     *
     * @param actor consuming actor
     * @return consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {

        actor.addStatus(new MelonBurstStatus(12));

        return actor
                + " consumes a Cosmic Melon. "
                + "An explosion will strike adjacent tiles next turn!";
    }
}
