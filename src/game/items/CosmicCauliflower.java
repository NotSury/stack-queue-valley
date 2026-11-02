package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import game.statuses.HealthOverTimeStatus;

/**
 * Cosmic Cauliflower provides temporary health regeneration.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CosmicCauliflower extends ConsumableItem {

    /**
     * Creates a portable Cosmic Cauliflower.
     */
    public CosmicCauliflower() {
        super("Cosmic Cauliflower", 'u');
    }

    /**
     * Restores five health each tick for two ticks.
     *
     * @param actor consuming actor
     * @return consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {

        actor.addStatus(new HealthOverTimeStatus(10, 2));

        return actor
                + " consumes a Cosmic Cauliflower and gains "
                + "10 HP regeneration for 2 turns.";
    }
}
