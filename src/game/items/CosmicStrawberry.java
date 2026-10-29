package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import game.statuses.RadiationImmunityStatus;

/**
 * Cosmic Strawberry grants two turns of radiation immunity.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CosmicStrawberry extends ConsumableItem {

    /**
     * Creates a portable Cosmic Strawberry.
     */
    public CosmicStrawberry() {
        super("Cosmic Strawberry", 'x');
    }

    /**
     * Refreshes radiation immunity to two ticks.
     *
     * @param actor consuming actor
     * @return consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {

        for (RadiationImmunityStatus status :
                actor.statusesOf(RadiationImmunityStatus.class)) {

            actor.removeStatus(status);
        }

        actor.addStatus(new RadiationImmunityStatus(2));

        return actor
                + " consumes a Cosmic Strawberry and gains "
                + "radiation immunity for 2 turns.";
    }
}
