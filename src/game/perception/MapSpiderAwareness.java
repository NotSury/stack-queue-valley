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
 * @version 1.2.0
 */
public class MapSpiderAwareness implements SpiderTargetTracker {

    private static final int NEST_THREAT_RADIUS = 3;
    /**
     * How far from an alert location a Farmer is still noticed.
     */
    private static final int ALERT_SEARCH_RADIUS = 2;


    /**
     * Entry webs placed when Weaving starts, used for the capacity check.
     */
    private static final int WEAVING_ENTRY_WEBS = 1;

    private final Actor creature;
    private final EnvironmentScanner scanner;
    private final WebPlanner webPlanner;
    private final JumpPlanner jumpPlanner;
    private final WebNetwork network;

    private final int detectionRadius;
    private final int leashRadius;
    /**
     * The Farmer currently being pursued, or null.
     * Kept so the Spider keeps tracking him inside the pursuit boundary
     * even after he moves beyond the normal detection radius.
     */
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
     * Returns the valid engaged Farmer, even beyond local detection range.
     * When no engagement exists, searches locally for an eligible Farmer.
     *
     * @param origin the Spider's current location
     * @return an eligible Farmer, or null when none is found
     */
    @Override
    public Actor findActiveFarmer(Location origin) {
        if (origin.map() != network.getNestLocation().map()) {
            return null;
        }

        Actor engaged = getEngagedTarget();

        if (engaged != null) {
            return engaged;
        }

        Actor detected = scanner.findNearestActorWith(
                origin,
                detectionRadius,
                ActorRole.FARMER
        );

        if (isValidHuntingTarget(detected)) {
            engagedFarmer = detected;
        }

        return engagedFarmer;
    }

    /**
     * Checks shared Hunting eligibility without requiring a local scan
     * or a currently available route.
     *
     * @param farmer the proposed target, or null
     * @return true when the target is a conscious Farmer inside the territory
     */
    @Override
    public boolean isValidHuntingTarget(Actor farmer) {
        Location nest = network.getNestLocation();

        return farmer != null
                && farmer.isConscious()
                && farmer.hasAbility(ActorRole.FARMER)
                && nest.map().contains(creature)
                && nest.map().contains(farmer)
                && GridDistance.between(nest, nest.map().locationOf(farmer)) <= leashRadius;
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
        return findFarmerNear(network.getNestLocation(), NEST_THREAT_RADIUS) != null;
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

        return findFarmerNear(alert.getLocation(), ALERT_SEARCH_RADIUS);
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
    @Override
    public Actor findFarmerNear(Location centre, int radius){
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


    /**
     * Finds a conscious Farmer within the nest-threat radius of the nest.
     *
     * @return the intruding Farmer, or null if the nest is undisturbed
     */
    @Override
    public Actor findNestIntruder() {
        return findFarmerNear(network.getNestLocation(), NEST_THREAT_RADIUS);
    }

    /**
     * Stores an eligible Farmer as the engaged target.
     * Clears the engagement when the supplied actor is ineligible.
     *
     * @param farmer the proposed target, or null
     */
    @Override
    public void engage(Actor farmer) {
        engagedFarmer = isValidHuntingTarget(farmer) ? farmer : null;
    }

    /**
     * Keeps the engaged Farmer while conscious, on the nest's map
     * and inside the pursuit boundary. A blocked route causes ordinary
     * Hunting to wait and retry instead of forgetting the target.
     *
     * @return the valid engaged Farmer, or null
     */
    @Override
    public Actor getEngagedTarget() {
        if (!isValidHuntingTarget(engagedFarmer)) {
            engagedFarmer = null;
        }

        return engagedFarmer;
    }

    /**
     * Checks for a reachable web site and room for one entry web plus
     * one ordinary web.
     *
     * @param origin the Spider's current location
     * @return true if the Spider can start weaving
     */
    @Override
    public boolean canStartWeaving(Location origin) {
        return webPlanner.hasReachableWeavingWork(creature, origin, network)
                && webPlanner.hasCapacityForWeaving(network, WEAVING_ENTRY_WEBS);
    }

    /**
     * Checks whether the latest alert can be reached by a useful jump or
     * a safe walk from the Spider's location.
     *
     * @param origin the Spider's current location
     * @return true if there is an alert and it can be approached
     */
    @Override
    public boolean canInvestigateAlert(Location origin) {
        Location alert = network.getAlertLocation();
        return alert != null && jumpPlanner.canApproach(creature, origin, alert);
    }

}
