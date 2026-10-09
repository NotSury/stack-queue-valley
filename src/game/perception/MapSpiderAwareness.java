package game.perception;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.navigation.JumpPlanner;
import game.navigation.WebPlanner;

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

    /**
     * Entry webs placed when Weaving starts, used for the capacity check.
     */
    private static final int WEAVING_ENTRY_WEBS = 1;

    private final Actor creature;
    private final EnvironmentScanner scanner;
    private final WebPlanner webPlanner;
    private final JumpPlanner jumpPlanner;
    private final WebNetwork network;

    private final int nestThreatRadius;
    private final int leashRadius;
    /**
     * The Farmer currently being pursued, or null.
     * Kept so the Spider keeps tracking him inside the pursuit boundary
     * even after he moves beyond the normal detection radius.
     */
    private Actor engagedFarmer;

    /**
     * Creates environmental assessment for one Spider.
     *
     * @param creature the owning Spider
     * @param scanner the Farmer detection collaborator
     * @param webPlanner the walking and placement planner
     * @param jumpPlanner the tactical jump planner
     * @param network the owning Spider's web network
     * @param nestThreatRadius the direct intrusion distance from the nest
     * @param leashRadius the maximum Farmer distance for continued Hunting
     */
    public MapSpiderAwareness(
            Actor creature,
            EnvironmentScanner scanner,
            WebPlanner webPlanner,
            JumpPlanner jumpPlanner,
            WebNetwork network,
            int nestThreatRadius,
            int leashRadius
    ) {
        this.creature = creature;
        this.scanner = scanner;
        this.webPlanner = webPlanner;
        this.jumpPlanner = jumpPlanner;
        this.network = network;
        this.nestThreatRadius = nestThreatRadius;
        this.leashRadius = leashRadius;
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
     * Finds a conscious Farmer within the nest-threat radius of the nest.
     *
     * @return the intruding Farmer, or null if the nest is undisturbed
     */
    @Override
    public Actor findNestIntruder() {
        return findFarmerNear(network.getNestLocation(), nestThreatRadius);
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
