package game.effects;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.support.AreaLocationHelper;

import java.util.List;

/**
 * Abstract base class for magical effects that last
 * for a limited number of turns within a fixed area.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public abstract class TimedAreaEffect
{
    private final GameMap map;
    private final Location centre;
    private final int radius;
    private int remainingTurns;
    private boolean finished = false;

    /**
     * Returns the game map containing this effect.
     *
     * @return the game map
     */
    protected GameMap getMap() {
        return map;
    }

    /**
     * Returns the fixed centre of this effect.
     *
     * @return the centre location
     */
    protected Location getCentre() {
        return centre;
    }

    /**
     * Creates a timed area effect.
     *
     * @param map the game map
     * @param centre the centre of the effect
     * @param radius the radius of the affected area
     * @param duration the number of effect ticks
     */
    protected TimedAreaEffect(GameMap map, Location centre,
                              int radius, int duration)
    {
        this.map = map;
        this.centre = centre;
        this.radius = radius;
        this.remainingTurns = duration;
    }

    /**
     * Returns the name of the magical effect.
     *
     * @return the effect name
     */
    public abstract String getAbilityName();

    /**
     * Applies the specific effect to locations in its area.
     *
     * @param area locations affected by the effect
     */
    protected abstract void applyTick(List<Location> area);

    /**
     * Performs cleanup when the effect expires.
     * Subclasses may override this method if cleanup is required.
     */
    protected void onExpire(){}

    /**
     * Applies one tick of the effect.
     */
    public void tick()
    {
        if (!finished && remainingTurns > 0)
        {
            List<Location> area =
                    AreaLocationHelper.square(map, centre, radius);

            applyTick(area);

            if (remainingTurns > 0)
            {
                remainingTurns--;
            }
        }
    }

    /**
     * Check if the effect has finished.
     *
     * @return true if no turns remain
     */
    public boolean isExpired()
    {
        return remainingTurns <= 0;
    }

    /**
     * Finishes the effect and performs cleanup only once.
     * Can be called after normal expiry or when the
     * companion dies before the effect expires.
     */
    public void finish()
    {
        if (!finished)
        {
            finished = true;
            remainingTurns = 0;
            onExpire();
        }
    }

    /**
     * Ends the effect early when its completion condition is met.
     */
    protected void finishEarly()
    {
        remainingTurns = 0;
    }

}
