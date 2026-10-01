package game.items;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents a Cauliflower that can be consumed by an actor.
 *
 * <p>Consuming a Cauliflower heals the actor by 25 health points.
 * There is also a 50 percent chance that a new Cauliflower is added
 * to the actor's inventory.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public class Cauliflower extends ConsumableItem {

    /**
     * Amount of health restored when consumed.
     */
    private static final int HEAL_AMOUNT = 25;

    /**
     * Chance of receiving another Cauliflower after consumption.
     */
    private static final double REPRODUCTION_CHANCE = 0.5;

    /**
     * Constructor for Cauliflower.
     */
    public Cauliflower() {
        super("Cauliflower", 'c');
    }

    /**
     * Applies the consumption effect of this Cauliflower.
     *
     * <p>The actor is healed by 25 health points. There is then a
     * 50 percent chance that a new Cauliflower is added to the
     * actor's inventory.</p>
     *
     * @param actor the actor consuming the Cauliflower
     * @return a description of the consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        actor.heal(HEAL_AMOUNT);

        if (Math.random() < REPRODUCTION_CHANCE) {
            actor.getInventory().add(new Cauliflower());

            return actor
                    + " consumes the Cauliflower, restores 25 health, "
                    + "and receives another Cauliflower.";
        }

        return actor
                + " consumes the Cauliflower and restores 25 health.";
    }
}