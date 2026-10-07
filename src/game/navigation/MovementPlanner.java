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
     * Finds the first step of a bounded route away from a threat.
     * The route destination must increase threat distance.
     * The first step may move sideways but never closer to the threat.
     * Movement remains on the origin map.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @return an escape exit, or null if no improving step exists
     */
    Exit findStepAway(Actor creature, Location origin, Location threat);

    /**
     * Finds an escape route while excluding a reserved destination.
     * The reserved tile must not appear anywhere along the route.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param threat the threatening actor's location
     * @param excludedExit the exit whose destination must be excluded, or null
     * when no destination is reserved
     * @return an escape exit, or null if no improving step exists
     */
    Exit findStepAway(Actor creature, Location origin, Location threat, Exit excludedExit);

    /**
     * Finds a valid adjacent exit matching a grid direction.
     *
     * @param creature the creature that would perform the movement
     * @param origin the creature's current location
     * @param direction the directional exit name supplied by the engine
     * @return a matching movement exit, or null if none is available
     */
    Exit findStepInDirection(Actor creature, Location origin, String direction);
}
