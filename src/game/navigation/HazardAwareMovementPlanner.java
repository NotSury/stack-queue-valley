package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A movement planner that walks towards targets only through safe tiles.
 *
 * <p>Wraps an ordinary movement planner (Decorator): stepping towards a
 * target uses a hazard-aware route that avoids fire and charged ground,
 * and every other movement query is passed to the wrapped planner
 * unchanged. This lets existing behaviours such as AmbushBehaviour walk
 * safely without being modified (Open-Closed Principle).</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class HazardAwareMovementPlanner implements MovementPlanner {

    /**
     * Planner used for every query except stepping towards a target.
     */
    private final MovementPlanner basePlanner;

    /**
     * Planner whose routes avoid hazardous tiles.
     */
    private final WebPlanner safeRouter;

    /**
     * Creates a hazard-aware planner.
     *
     * @param basePlanner the planner used for all other movement queries
     * @param safeRouter the planner that finds hazard-free routes
     */
    public HazardAwareMovementPlanner(MovementPlanner basePlanner, WebPlanner safeRouter) {
        this.basePlanner = basePlanner;
        this.safeRouter = safeRouter;
    }

    /**
     * Finds the first step of a hazard-free route towards a target.
     *
     * @param creature the creature that would move
     * @param origin the creature's current location
     * @param target the location to approach
     * @return the first safe step, or null if no safe route exists
     */
    @Override
    public Exit findStepTowards(Actor creature, Location origin, Location target) {
        return safeRouter.findStepTowards(creature, origin, target);
    }

    /**
     * Passes the query to the wrapped planner.
     *
     * @param creature the creature that would move
     * @param origin the creature's current location
     * @param threat the location to move away from
     * @return the step chosen by the wrapped planner
     */
    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat) {
        return basePlanner.findStepAway(creature, origin, threat);
    }

    /**
     * Passes the query to the wrapped planner.
     *
     * @param creature the creature that would move
     * @param origin the creature's current location
     * @param threat the location to move away from
     * @param excludedExit an exit that must not be chosen
     * @return the step chosen by the wrapped planner
     */
    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat, Exit excludedExit) {
        return basePlanner.findStepAway(creature, origin, threat, excludedExit);
    }

    /**
     * Passes the query to the wrapped planner.
     *
     * @param creature the creature that would move
     * @param origin the creature's current location
     * @param direction the direction to move in
     * @return the step chosen by the wrapped planner
     */
    @Override
    public Exit findStepInDirection(Actor creature, Location origin, String direction) {
        return basePlanner.findStepInDirection(creature, origin, direction);
    }

    /**
     * Passes the query to the wrapped planner.
     *
     * @param creature the creature that would escape
     * @param origin the creature's current location
     * @param threat the location to escape from
     * @return the plan chosen by the wrapped planner
     */
    @Override
    public EscapePlan planEscape(Actor creature, Location origin, Location threat) {
        return basePlanner.planEscape(creature, origin, threat);
    }
}