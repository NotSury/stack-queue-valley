package game.utils;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Provides common formatting for plant output messages.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public final class PlantMessages {
    private PlantMessages(){
        throw new UnsupportedOperationException(
                "PlantMessages is a utility class and cannot be instantiated."
        );
    }

    /**
     * Formats the coordinates and map of a location.
     *
     * @param location the location to format.
     * @return formatted location description.
     */
    public static String formatLocation(Location location){
        return "(" + location.x() + ", " + location.y() + ") on " + location.map();
    }
}
