package game.utils;

import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.PlantableGround;

import java.util.List;

/**
 * This is a utility class for finding adjacent plantable Locations.
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public class PlantingLocations
{
    /**
     * Prevents instantiation of this utility class.
     */
    private PlantingLocations()
    {
        throw new UnsupportedOperationException(
                "PlantingLocations is a utility class and cannot be instantiated."
        );
    }

    /**
     * Finds adjacent plantable tiles.
     *
     * @param center the location to search around
     * @param requireUnoccupied whether tiles containing actors are excluded
     * @return a list of suitable adjacent locations
     */
    public static List<Location> plantableTilesAround(
            Location center, boolean requireUnoccupied
    ) {
        return AdjacentLocations.matchingAround(
                center,
                location -> location.getGroundAs(PlantableGround.class) != null
                && (!requireUnoccupied || !location.containsAnActor())
        );
    }
}
