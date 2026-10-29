package game.items;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Cosmic Parsnip restores health when consumed.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CosmicParsnip extends ConsumableItem {

    /**
     * Creates a portable Cosmic Parsnip.
     */
    public CosmicParsnip() {
        super("Cosmic Parsnip", 'p');
    }

    /**
     * Restores up to 30 health.
     *
     * @param actor consuming actor
     * @return consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        actor.heal(30);

        return actor
                + " consumes a Cosmic Parsnip and restores up to 30 HP.";
    }
}
