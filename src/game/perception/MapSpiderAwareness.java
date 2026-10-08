package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.navigation.JumpPlanner;
import game.navigation.WebPlanner;
import game.traps.WebAlert;

/**
 * Shares Farmer detection, nest-threat assessment and navigation queries
 * between all four Spider states.
 * Uses injected planners to assess safe walking, useful jumps and
 * reachable web-placement opportunities without changing the map.
 *
 * @author Surya Muni Peragas 35651628
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public class MapSpiderAwareness implements SpiderTargetTracker {

    private static final int NEST_THREAT_RADIUS = 3;
    private static final int ALERT_SEARCH_RADIUS = 2;

    private final Actor creature;
    private final EnvironmentScanner scanner;
    private final WebPlanner webPlanner;
    private final JumpPlanner jumpPlanner;
    private final WebNetwork network;

    private final int detectionRadius;
    private final int leashRadius;
    private Actor engagedFarmer;

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
     * A Farmer already engaged keeps being tracked while he stays inside
     * the pursuit boundary, even beyond the detection radius.
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

        if (isPursuable(origin, engagedFarmer)){
            return engagedFarmer;
        }

        Actor farmer = scanner.findNearestActorWith(
                origin,
                detectionRadius,
                ActorRole.FARMER
        );

        engagedFarmer = isPursuable(origin, farmer) ? farmer : null;
        return engagedFarmer;
    }

    /**
     * Checks whether a Farmer can be pursued from the Spider's location:
     * conscious, on the nest's map, inside the pursuit boundary, and
     * adjacent or approachable by a safe walk or a useful jump.
     *
     * @param origin the Spider's current location
     * @param farmer the Farmer being checked, or null
     * @return true if the Farmer can be pursued
     */
    private boolean isPursuable(Location origin, Actor farmer) {
        Location nest = network.getNestLocation();

        if (farmer == null || ! farmer.isConscious() || ! nest.map().contains((farmer))){
            return false;
        }
        Location target = nest.map().locationOf(farmer);

        if (GridDistance.between(target, nest) > leashRadius) {
            return false;
        }

        return canWalkToTarget(origin, target) || findUsefulLanding(origin, target) != null;
    }

    /**
     * Checks whether a conscious Farmer threatens the Spider's nest.
     * Requires the Farmer to be on the nest's map and within the configured
     * pursuit distance. The Farmer is considered an intruder when within
     * three tiles of the nest. Web triggers are reported through alerts,
     * so binding status is no longer used as evidence.
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
                && (nestDistance <= NEST_THREAT_RADIUS);
    }

    /**
     * Checks for a conscious Farmer within the nest-threat radius of the
     * nest, searching from the nest itself rather than from the Spider.
     *
     * @return true if the nest is intruded
     */
    @Override
    public boolean isNestIntruded(){
        return findFarmerAround(network.getNestLocation(), NEST_THREAT_RADIUS) != null;
    }


    /**
     * Finds the nearest conscious Farmer within two tiles of an alert.
     *
     * @param alert the alert being investigated, or null
     * @return the Farmer near the alert, or null if none
     */
    @Override
    public Actor findFarmerNearAlert(WebAlert alert){
        if (alert == null){
            return null;
        }

        return findFarmerAround(alert.getLocation(), ALERT_SEARCH_RADIUS);
    }

    /**
     * Finds a conscious Farmer on a location or within a radius of it.
     *
     * <p>The centre tile is checked first because the scanner only looks
     * at the tiles around it.</p>
     *
     * @param centre the location to search around
     * @param radius how far from the centre to search
     * @return the Farmer found, or null if none
     */
    private Actor findFarmerAround(Location centre, int radius){
        if (centre.containsAnActor()){
            Actor actor = centre.getActor();
            if (actor.hasAbility(ActorRole.FARMER) && actor.isConscious()){
                return actor;
            }
        }
        return scanner.findNearestActorWith(centre, radius, ActorRole.FARMER);
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

    /**
     * Finds the active Farmer; same as findActiveFarmer.
     *
     * @param origin the owning Spider's current location
     * @return the active Farmer, or null
     */
    @Override
    public Actor findActiveTarget(Location origin) {
        return findActiveFarmer(origin);
    }
}
