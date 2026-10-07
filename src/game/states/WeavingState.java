package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.WebNetwork;
import game.navigation.WebPlanner;
import game.perception.EnvironmentScanner;
import game.perception.SpiderAwareness;

/**
 * Expands the nest's web network through targeted trap placement.
 * Enters Hunting for an intruder or Nesting when both the Farmer
 * and available web sites are absent.
 */
public class WeavingState extends BehaviourDrivenState {

    private static final int MAXIMUM_ENTRY_WEBS = 2;
    private static final int ENTRY_RADIUS = 2;

    private final WebNetwork network;
    private final WebPlanner planner;
    private final SpiderAwareness awareness;

    /**
     * Creates a weaving state using the owning Spider's collaborators.
     *
     * @param creature the Spider that owns this state
     * @param scanner the environmental scanner
     * @param network the owning Spider's web network
     * @param planner the safe walking and web-site planner
     * @param awareness the shared Farmer assessment
     * @param weavingBehaviour the behaviour used for ordinary web placement
     */
    public WeavingState(
            Actor creature,
            EnvironmentScanner scanner,
            WebNetwork network,
            WebPlanner planner,
            SpiderAwareness awareness,
            Behaviour<Actor, Action> weavingBehaviour
    ) {
        super(creature, scanner, weavingBehaviour);
        this.network = network;
        this.planner = planner;
        this.awareness = awareness;
    }

    /**
     * Returns the identifier used to register and select the Weaving state.
     *
     * @return the Weaving state identifier
     */
    @Override
    public CreatureStateType getType() {
        return CreatureStateType.WEAVING;
    }

    /**
     * Instantly spreads up to two webs onto reachable nearby approach sites.
     * These entry webs are separate from ordinary turn-by-turn weaving.
     *
     * @param location the Spider's location when entering this state
     * @return a description of the entry effect
     */
    @Override
    public String onEnter(Location location) {
        int placed = 0;

        for (Location tile : location.getNearbyLocations(ENTRY_RADIUS)) {
            if (!planner.isPlacementTargetValid(
                    getCreature(), location, tile, network)) {
                continue;
            }

            if (network.placeAt(tile)) {
                placed++;
            }

            if (placed == MAXIMUM_ENTRY_WEBS) {
                break;
            }
        }

        return getCreature() + " spreads " + placed
                + " approach webs around its nest.";
    }

    /**
     * Prioritizes intruders, then checks whether useful weaving remains.
     * Returns to Nesting when no active Farmer or eligible reachable site exists.
     *
     * @param location the Spider's current location
     * @return Hunting, Nesting or Weaving
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = awareness.findActiveFarmer(location);

        if (awareness.isIntruder(farmer)) {
            return CreatureStateType.HUNTING;
        }

        if (farmer == null
                && planner.findPlacementTarget(
                getCreature(), location, network) == null) {
            return CreatureStateType.NESTING;
        }

        return CreatureStateType.WEAVING;
    }


}
