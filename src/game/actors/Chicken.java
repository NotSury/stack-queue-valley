package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Egg;

import java.util.Random;

/**
 * Represents a Chicken that wanders around the farm and can be fed.
 * When fed, the Chicken restores health and produces Eggs.
 */
public class Chicken extends FarmAnimal {

    private static final int HEALTH_RESTORED_WHEN_FED = 3;
    private static final double BONUS_EGG_CHANCE = 0.5;

    private final Random random = new Random();

    /**
     * Constructs a Chicken with its initial health and display character.
     */
    public Chicken() {
        super("Chicken", 'c', 10);
    }

    /**
     * Applies the Chicken specific effect of being fed.
     * Feeding restores health of the Chicken and produces one Egg on a
     * randomly selected location adjacent to the feeding actor. There is also
     * a 50 percent chance that another Egg is produced.
     *
     * @param actor the actor feeding this chicken
     * @param actorLocation the location of the actor performing the feeding
     */
    @Override
    public void fedBy(Actor actor, Location actorLocation) {
        this.heal(HEALTH_RESTORED_WHEN_FED);

        dropItemNearby(actorLocation, new Egg());

        if (random.nextDouble() < BONUS_EGG_CHANCE) {
            dropItemNearby(actorLocation, new Egg());
        }
    }
}
