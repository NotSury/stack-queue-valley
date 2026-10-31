package game.effects;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.interfaces.Hostile;
import game.interfaces.Stunnable;

import java.util.List;
import java.util.Optional;

/**
 * A magical storm that damages and stuns hostile creatures
 * within a fixed 5x5 area for three effect ticks
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class RainbowStormEffect extends TimedAreaEffect{
    public static final String NAME = "Rainbow Storm";
    private static final int RADIUS = 2;
    private static final int DURATION = 3;
    private static final int DAMAGE = 5;
    private static final int STUN_DURATION = 1;

    /**
     * Creates a Rainbow Storm centred on a fixed location.
     *
     * @param map the current game map
     * @param centre the storm's centre at activation
     */
    public RainbowStormEffect(GameMap map, Location centre)
    {
        super(map, centre, RADIUS, DURATION);
    }

    /**
     * Returns the name of this effect.
     *
     * @return the Rainbow Storm
     */
    @Override
    public String getAbilityName() {
        return NAME;
    }

    /**
     * Damages and stuns hostile creatures inside the storm area.
     *
     * @param area locations affected by the effect
     */
    @Override
    protected void applyTick(List<Location> area) {
        for (Location location : area)
        {
            if (!location.containsAnActor())
            {
                continue;
            }
            Actor actor = location.getActor();

            Optional<Hostile> hostile =
                    actor.asCapability(Hostile.class);

            Optional<Stunnable> stunnable =
                    actor.asCapability(Stunnable.class);

            if (hostile.isPresent() && stunnable.isPresent())
            {
                actor.modifyStatistic(
                        ActorStatistics.HEALTH,
                        StatisticOperations.DECREASE,
                        DAMAGE
                );

                if (actor.isConscious())
                {
                    stunnable.get().stun(STUN_DURATION);
                }
            }
        }

    }
}
