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
 * Assesses reachable Farmers using the Spider's fixed nest and pursuit limits.
 * Accepts walking or a useful jump as an approach to the Farmer.
 */
public class MapSpiderAwareness implements SpiderAwareness {

    private static final int NEST_THREAT_RADIUS = 3;

    private final Actor creature;
    private final EnvironmentScanner scanner;
    private final WebPlanner webPlanner;
    private final JumpPlanner jumpPlanner;
    private final WebNetwork network;

    private final int detectionRadius;
    private final int leashRadius;

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
     * Does not move actors or change the environment.
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

        if (GridDistance.between(origin, target) == 1
                || webPlanner.findStepTowards(
                creature, origin, target) != null
                || jumpPlanner.findBestLanding(
                creature, origin, target) != null) {
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
}
