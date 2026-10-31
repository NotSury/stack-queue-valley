package game.magic;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.companions.MagicalCompanion;
import game.interfaces.GrowthAcceleratable;
import game.support.AreaLocationHelper;

import java.util.Optional;

/**
 * Guarantees one growth-stage advancement for eligible plants
 * within a 3x3 area centred on the magical companion.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class GrowthMagic implements FarmMagic
{
    private static  final int RADIUS = 1;

    /**
     * Returns the magic ability's name
     *
     * @return the ability name
     */
    @Override
    public String getName()
    {
        return "Growth Magic";
    }

    /**
     * Attempts to advance every eligible plant by one growth stage
     *
     * @param farmer the Farmer activating this magic
     * @param companion the companion providing the magic
     * @param map the game map
     * @return true if at least one plant grows, or else false
     */
    @Override
    public boolean cast(Actor farmer, MagicalCompanion companion,
                        GameMap map)
    {
        int grown = 0;

        Location centre = map.locationOf(companion);

        for (Location location :
        AreaLocationHelper.square(map, centre, RADIUS))
        {
            Optional<GrowthAcceleratable> plant =
                    location.getGround().asCapability(GrowthAcceleratable.class);

            boolean advanced = false;

            if (plant.isPresent()) {
                advanced = plant.get().advanceGrowth(location);

                if (advanced) {
                    grown++;
                }
            }
        }
        return grown > 0;
    }
}
