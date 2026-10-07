package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.navigation.JumpPlanner;
import game.navigation.WebPlanner;
import game.statuses.WebBoundStatus;

/**
 * Shares Farmer detection, nest-threat assessment and navigation queries
 * between all four Spider states.
 * Uses injected planners to assess safe walking, useful jumps and
 * reachable web-placement opportunities without changing the map.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public class MapSpiderAwareness implements SpiderTargetTracker {

    private static final int NEST_THREAT_RADIUS = 3;

    private final Actor creature;
    private final EnvironmentScanner scanner;
    private final WebPlanner webPlanner;
    private final JumpPlanner jumpPlanner;
    private final WebNetwork network;

    private final int detectionRadius;
    private final int leashRadius;

    /**
     * Creates shared environmental assessment for one Spider.
     * All supplied collaborators must operate on the same owning creature.
     *
     * @param creature the owning Spider
     * @param scanner the scanner used to detect Farmers
     * @param webPlanner the safe walking and web-placement planner
     * @param jumpPlanner the tactical jump planner
     * @param network the owning Spider's web network
     * @param detectionRadius the maximum Farmer detection distance
     * @param leashRadius the maximum pursuit distance from the nest
     */
    public MapSpiderAwareness(
            Actor creature,
            EnvironmentScanner scanner,
            WebPlanner webPlanner,
            JumpPlanner jumpPlanner,
            WebNetwork network,
            int detectionRadius,
            int leashRadius
    ) {
        this.creature = creature;
        this.scanner = scanner;
        this.webPlanner = webPlanner;
        this.jumpPlanner = jumpPlanner;
        this.network = network;
        this.detectionRadius = detectionRadius;
        this.leashRadius = leashRadius;
    }

    /**
     * Finds the nearest conscious Farmer within the detection radius.
     * Requires both the Spider and Farmer to be within the configured
     * pursuit distance from the fixed nest.
     * Accepts the Farmer when adjacent, approachable through safe walking,
     * or approachable through a useful tactical jump.
     *
     * @param origin the owning Spider's current location
     * @return the detected approachable Farmer, or null when none qualifies
     */
    @Override
    public Actor findActiveFarmer(Location origin) {
        Location nest = network.getNestLocation();

        if (origin.map() != nest.map()
                || GridDistance.between(origin, nest) > leashRadius) {
            return null;
        }

        Actor farmer = scanner.findNearestActorWith(
                origin,
                detectionRadius,
                ActorRole.FARMER
        );

        if (farmer == null) {
            return null;
        }

        Location target = origin.map().locationOf(farmer);

        if (GridDistance.between(target, nest) > leashRadius) {
            return null;
        }

        if (canWalkToTarget(origin, target)
                || findUsefulLanding(origin, target) != null) {
            return farmer;
        }

        return null;
    }

    /**
     * Checks whether a conscious Farmer threatens the Spider's nest.
     * Requires the Farmer to be on the nest's map and within the configured
     * pursuit distance. The Farmer is considered an intruder when within
     * three tiles of the nest or currently affected by WebBoundStatus.
     * Does not change the Farmer's statuses or the environment.
     *
     * @param farmer the actor being assessed, or null
     * @return true when the actor is a qualifying Farmer intruder
     */
    @Override
    public boolean isIntruder(Actor farmer) {
        Location nest = network.getNestLocation();

        if (farmer == null
                || !farmer.isConscious()
                || !farmer.hasAbility(ActorRole.FARMER)
                || !nest.map().contains(farmer)) {
            return false;
        }

        int nestDistance = GridDistance.between(
                nest,
                nest.map().locationOf(farmer)
        );

        return nestDistance <= leashRadius
                && (nestDistance <= NEST_THREAT_RADIUS
                || farmer.hasStatus(WebBoundStatus.class));
    }

    /**
     * Checks whether a target is adjacent or can be approached through
     * a safe walking route on the same map.
     *
     * @param origin the Spider's current location
     * @param target the target's location
     * @return true when a walking approach is available
     */
    @Override
    public boolean canWalkToTarget(Location origin, Location target) {
        if (origin.map() != target.map()) {
            return false;
        }

        return GridDistance.between(origin, target) == 1 || webPlanner.findStepTowards(creature, origin, target) != null;
    }

    /**
     * Finds a legal jump landing that improves the approach to a target.
     * Rejects targets located on another map.
     *
     * @param origin the Spider's current location
     * @param target the target's location
     * @return a useful landing, or null when none exists
     */
    @Override
    public Location findUsefulLanding(Location origin, Location target) {
        if (origin.map() != target.map()) {
            return null;
        }

        return jumpPlanner.findBestLanding(
                creature,
                origin,
                target
        );
    }

    /**
     * Checks for an eligible reachable web site using the shared planner.
     * Placement eligibility includes the web network's active-web limit.
     *
     * @param origin the Spider's current location
     * @return true when a reachable placement opportunity exists
     */
    @Override
    public boolean hasWebGap(Location origin) {
        return webPlanner.findPlacementTarget(
                creature,
                origin,
                network
        ) != null;
    }

    @Override
    public Actor findActiveTarget(Location origin) {
        return findActiveFarmer(origin);
    }
}
