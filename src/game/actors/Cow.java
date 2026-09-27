package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Milk;

/**
 * Represents a Cow that can wander around the farm and be fed.
 */
public class Cow extends FarmAnimal {

    private static final int HEALTH_RESTORED_WHEN_FED = 5;

    /**
     * Constructs a Cow with 20 health points.
     */
    public Cow() {
        super("Cow", 'C', 20);
    }

    /**
     * Applies the Cow specific effect of being fed.
     * Feeding restores health to the Cow and produces one bottle of Milk on
     * a randomly selected location adjacent to the feeding actor.
     *
     * @param actor the actor performing the feeding
     * @param actorLocation the location of the actor performing the feeding
     */
    @Override
    public void fedBy(Actor actor, Location actorLocation) {
        this.heal(HEALTH_RESTORED_WHEN_FED);
        dropItemNearby(actorLocation, new Milk());
    }
}
