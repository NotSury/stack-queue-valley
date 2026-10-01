package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Represents a Melon that can be consumed by an actor.
 *
 * <p>Consuming a Melon increases the actor's maximum health
 * by 10 points.</p>
 *
 * @author <Chin Kai Xuan> <35730374>
 * @version 1.0.0
 */
public class Melon extends ConsumableItem {

    /**
     * Amount by which maximum health is increased when consumed.
     */
    private static final int MAX_HEALTH_INCREASE = 10;

    /**
     * Constructor for Melon.
     */
    public Melon() {
        super("Melon", 'm');
    }

    /**
     * Applies the consumption effect of this Melon.
     *
     * <p>The actor's maximum health is increased by 10 points.</p>
     *
     * @param actor the actor consuming the Melon
     * @return a description of the consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        actor.modifyStatisticMaximum(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                MAX_HEALTH_INCREASE
        );

        return actor + " consumes the Melon and increases maximum health by 10.";
    }
}