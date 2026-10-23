package game.utils;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Exit;
import game.interfaces.PlantableGround;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a utility class for finding adjacent plantable Locations.
 *
 * @author Vanessa Liau Jia Yee <34599398>
 * @version 1.0.0
 */
public class PlantingLocations
{
    private PlantingLocations()
    {
    }

    /**
     * Finds adjacent plantable tiles.
     *
     * @param center the location to search around
     * @param requireUnoccupied whether tiles containing actors are excluded
     * @return a list of suitable adjacent locations
     */
    public static List<Location> plantableTilesAround(
            Location center, boolean requireUnoccupied)
    {
        List<Location> availableLocations = new ArrayList<>();

        for (Exit exit : center.getExits())
        {
            Location location = exit.getDestination();

            if (location.getGroundAs(PlantableGround.class) != null
            && (!requireUnoccupied || !location.containsAnActor()))
            {
                availableLocations.add(location);
            }
        }
        return availableLocations;
    }
}
