package game.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.Repellable;
import game.support.AreaLocationHelper;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

/**
 * A temporary sanctuary created by SanctuaryDeer.
 * Protects its area and supports the Farmer.
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.1.0
 */
public class NatureSanctuaryEffect extends TimedAreaEffect {
    public static final String NAME = "Nature's Sanctuary";

    private static final int RADIUS = 1;
    private static final int DURATION = 2;
    private final Actor farmer;
    private List<Location> protectedArea;

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
     * Repels hostile creature inside the sanctuary and prevents
     * nearby hostile creature from entering its protected area.
     *
     * @param area the sanctuary's protected locations
     */
    private void repelHostiles(List<Location> area) {
        GameMap map = area.get(0).map();

        for (Location location : area) {
            if (!location.containsAnActor()) {
                continue;
            }
            Actor actor = location.getActor();

            Optional<Repellable> repellable =
                    actor.asCapability(Repellable.class);

            if (repellable.isEmpty()) {
                continue;
            }

            boolean moved = false;

            for (int distance = 1; distance <= 5 && !moved; distance++) {
                for (Location candidate : AreaLocationHelper.square(map, location, distance)) {
                    if (area.contains(candidate) || !candidate.canActorEnter(actor)) {
                        continue;
                    }

                    int dx = Math.abs(candidate.x() - location.x());
                    int dy = Math.abs(candidate.y() - location.y());

                    if (Math.max(dx, dy) != distance) {
                        continue;
                    }

                    map.moveActor(actor, candidate);
                    moved = true;
                    break;
                }
            }
            repellable.get().forbid(area);
        }
    }

    /**
     * Returns the name of Nature's Sanctuary.
     *
     * @return the sanctuary ability name
     */
    @Override
    public String getAbilityName() {
        return NAME;
    }

    /**
     * Restores the Farmer's health and stamina when the Farmer
     * is inside the sanctuary area.
     *
     * @param area locations affected by the effect
     */
    @Override
    protected void applyTick(List<Location> area) {
    }

    /**
     * Activates sanctuary protection and restores the Farmer's health and stamina when the Farmer
     * is inside the sanctuary area.
     *
     * @param area locations covered by the sanctuary
     */
    public void activate(List<Location> area) {
        protectedArea = new ArrayList<>(area);
        repelHostiles(area);

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
