package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

/**
 * A cooked dish that restores stamina according to its fruit count.
 *
 * <p>Each Jam stores the number of fruits used in its own cooking attempt.
 * Stamina restoration is capped by the actor's existing statistic.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class Jam extends ConsumableItem {

    private final int fruitCount;

    /**
     * Creates a Jam with its own fruit count.
     *
     * @param fruitCount the positive number of fruits used to cook this Jam
     */
    public Jam(int fruitCount) {
        super("Jam", 'J');
        this.fruitCount = fruitCount;
    }

    /**
     * Restores stamina according to the number of fruits used.
     *
     * @param actor the actor consuming the Jam
     * @return a description including the actual stamina restored
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        int staminaBefore = actor.getStatistic(ActorStatistics.STAMINA);

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                fruitCount);

        int restored =
                actor.getStatistic(ActorStatistics.STAMINA) - staminaBefore;

        return actor + " consumes Jam and restores "
                + restored + " stamina.";
    }
}