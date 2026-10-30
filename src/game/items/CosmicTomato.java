package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * Cosmic Tomato replenishes stamina on consumption.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CosmicTomato extends ConsumableItem {

    /**
     * Creates a portable Cosmic Tomato.
     */
    public CosmicTomato() {
        super("Cosmic Tomato", 't');
    }

    /**
     * Restores up to eight stamina to actors with stamina.
     *
     * @param actor consuming actor
     * @return consumption result
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {

        if (!actor.hasStatistic(ActorStatistics.STAMINA)) {
            return actor
                    + " consumes a Cosmic Tomato, but has no stamina to restore.";
        }

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                8
        );

        return actor
                + " consumes a Cosmic Tomato and restores up to 8 stamina.";
    }
}
