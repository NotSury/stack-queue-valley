package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Plans movement steps for creatures.
 * Queries inspect the current map without moving actors or changing the environment.
 */
public interface MovementPlanner {

    /**
     * Finds the first step along a shortest valid route to a target.
     * An occupied target is approached by reaching an adjacent tile.
     * Movement remains on the origin map and uses adjacent exits.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param target the location to reach or approach
     * @return the first exit, or null if no movement is needed or no valid route exists
     */
    Exit findStepTowards(Actor creature, Location origin, Location target);

    /**
     * Selects a valid adjacent step that increases distance from a threat.
     * Chooses the step with the greatest resulting distance.
     * Movement remains on the origin map.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @return an escape exit, or null if no improving step exists
     */
    Exit findStepAway(Actor creature, Location origin, Location threat);
}
