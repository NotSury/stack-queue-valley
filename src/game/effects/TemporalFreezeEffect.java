package game.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Freezable;

import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates a temporary freeze field that suspends plant
 * lifecycle behaviours in a 3x3 area for three effect ticks.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class TemporalFreezeEffect extends TimedAreaEffect
{
    public static final String NAME = "Temporal Freeze";

    private static final int RADIUS = 1;
    private static final int DURATION = 3;
    private final Set<Freezable> heldPlants = new HashSet<>();

    /**
     * Creates a Temporal Freeze effect.
     *
     * @param map the game map
     * @param centre the rabbit's Location when activated
     */
    public TemporalFreezeEffect(GameMap map, Location centre)
    {
        super(map, centre, RADIUS, DURATION);
    }

    /**
     * Returns the ability name.
     *
     * @return the ability name
     */
    @Override
    public String getAbilityName()
    {
        return NAME;
    }

    /**
     * Applies the freeze effect to eligible plants.
     *
     * @param area locations within the freeze field
     */
    @Override
    protected void applyTick(List<Location> area)
    {
        for (Location location : area)
        {
            Optional<Freezable> plant =
                    location.getGround().asCapability(Freezable.class);

            if (plant.isPresent())
            {
                Freezable freezable = plant.get();

                freezable.freeze();

                if (!heldPlants.contains(freezable))
                {
                    heldPlants.add(freezable);
                    freezable.holdFreeze();
                }
            }
        }
    }

    /**
     * Releases all plants held by this freeze effect when it expires.
     */
    @Override
    protected void onExpire() {
        for (Freezable plant : heldPlants)
        {
            plant.releaseFreeze();
        }
        heldPlants.clear();
    }
}
