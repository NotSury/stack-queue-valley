package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A cooked dish that increases maximum health and stamina,
 * then fully restores both statistics.
 */
public class HarvestFeast extends ConsumableItem {

    private static final int MAXIMUM_INCREASE = 1;

    /**
     * Creates a fresh Harvest Feast.
     */
    public HarvestFeast() {
        super("Harvest Feast", 'H');
    }

    /**
     * Increases maximum health and stamina and restores both
     * to their new maximum values.
     *
     * @param actor the actor consuming this Harvest Feast
     * @return a description of the consumption effect
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        actor.modifyStatisticMaximum(
                ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                MAXIMUM_INCREASE
        );

        actor.modifyStatisticMaximum(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                MAXIMUM_INCREASE
        );

        return actor + " consumes a Harvest Feast, increasing maximum "
                + "health and stamina by " + MAXIMUM_INCREASE
                + " and fully restoring both.";
    }
}
