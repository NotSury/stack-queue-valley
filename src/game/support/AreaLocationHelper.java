package game.support;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for finding valid map locations
 * within a square area.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public final class AreaLocationHelper
{
    private AreaLocationHelper()
    {}

    /**
     * This checks if a coordinate is inside the map.
     *
     * @param map the game map
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the coordinate is within the map
     */
    public static boolean inBounds(GameMap map, int x, int y)
    {
        return map.getXRange().contains(x)
                && map.getYRange().contains(y);
    }

    /**
     * This returns all valid locations within a square area.
     *
     * @param map the game map
     * @param centre centre of the area
     * @param radius radius of the square
     * @return list of valid locations
     */
    public static List<Location> square(GameMap map,
                                        Location centre,
                                        int radius)
    {
        List<Location> locations = new ArrayList<>();

        int startX = centre.x() - radius;
        int endX = centre.x() + radius;

        int startY = centre.y() - radius;
        int endY = centre.y() + radius;

        for (int x = startX; x <= endX; x++)
        {
            for (int y = startY; y <= endX; y++)
            {
                if (inBounds(map, x, y))
                {
                    locations.add(map.at(x, y));
                }
            }
        }
        return locations;
    }
}
