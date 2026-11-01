package game.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import java.util.List;

/**
 * A temporary sanctuary created by SanctuaryDeer.
 * Protects its area and supports the Farmer.
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public class NatureSanctuaryEffect extends TimedAreaEffect {
    public static final String NAME = "Nature's Sanctuary";

    private static final int RADIUS = 1;
    private static final int DURATION = 1;
    private final Actor farmer;

    /**
     * Creates Nature's Sanctuary.
     *
     * @param map the current game map
     * @param centre the sanctuary's centre
     * @param farmer the Farmer activating the ability
     */
    public NatureSanctuaryEffect(GameMap map, Location centre, Actor farmer)
    {
        super(map, centre, RADIUS, DURATION);
        this.farmer = farmer;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAbilityName() {
        return "";
    }

    /**
     * Restores the Farmer's health and stamina when the Farmer
     * is inside the sanctuary area.
     *
     * @param area locations affected by the effect
     */
    @Override
    protected void applyTick(List<Location> area) {
        for (Location location : area) {
            if (location.containsAnActor() &&
            location.getActor() == farmer) {

                farmer.modifyStatistic(ActorStatistics.HEALTH,
                        StatisticOperations.INCREASE, 2);

                farmer.modifyStatistic(ActorStatistics.STAMINA,
                        StatisticOperations.INCREASE, 2);

                return;
            }
        }
    }
}
