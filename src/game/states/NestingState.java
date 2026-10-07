package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.WebNetwork;
import game.navigation.WebPlanner;
import game.perception.EnvironmentScanner;
import game.perception.SpiderAwareness;

/**
 * Guards a fixed nest and returns to it using safe walking routes.
 * Enters Hunting for an intruder or Weaving after quiet turns at the nest.
 */
public class NestingState extends EnvironmentAwareState {

    private static final int REQUIRED_QUIET_TURNS = 3;
    private static final int MAXIMUM_GUARD_WEBS = 3;

    private final WebNetwork network;
    private final WebPlanner planner;
    private final SpiderAwareness awareness;

    private int quietTurns;

    /**
     * Creates a nesting state using the owning Spider's collaborators.
     *
     * @param creature the Spider that owns this state
     * @param scanner the environmental scanner
     * @param network the owning Spider's web network
     * @param planner the safe walking and web-site planner
     * @param awareness the shared Farmer assessment
     */
    public NestingState(
            Actor creature,
            EnvironmentScanner scanner,
            WebNetwork network,
            WebPlanner planner,
            SpiderAwareness awareness
    ) {
        super(creature, scanner);
        this.network = network;
        this.planner = planner;
        this.awareness = awareness;
    }

    /**
     * Returns the identifier used to register and select the Nesting state.
     *
     * @return the Nesting state identifier
     */
    @Override
    public CreatureStateType getType() {
        return CreatureStateType.NESTING;
    }

    /**
     * Places up to three guard webs beside the fixed nest and resets quiet time.
     *
     * @param location the Spider's location when entering this state
     * @return a description of the entry effect
     */
    @Override
    public String onEnter(Location location) {
        quietTurns = 0;
        int placed = 0;

        for (Location tile
                : network.getNestLocation().getNearbyLocations(1)) {
            if (network.placeAt(tile)) {
                placed++;
            }

            if (placed == MAXIMUM_GUARD_WEBS) {
                break;
            }
        }

        return getCreature() + " reinforces its nest with "
                + placed + " guard webs.";
    }

    /**
     * Evaluates intruders, quiet time and reachable web-placement sites.
     * Quiet time advances only while at the nest without an active Farmer.
     *
     * @param location the Spider's current location
     * @return Hunting, Weaving or Nesting
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = awareness.findActiveFarmer(location);

        if (awareness.isIntruder(farmer)) {
            return CreatureStateType.HUNTING;
        }

        if (farmer != null || location != network.getNestLocation()) {
            quietTurns = 0;
            return CreatureStateType.NESTING;
        }

        quietTurns++;

        if (quietTurns >= REQUIRED_QUIET_TURNS
                && planner.findPlacementTarget(
                getCreature(), location, network) != null) {
            return CreatureStateType.WEAVING;
        }

        return CreatureStateType.NESTING;
    }

    /**
     * Returns towards the nest or remains stationary when already home.
     *
     * @param location the Spider's current location
     * @return a safe walking action or an idle action
     */
    @Override
    public Action getAction(Location location) {
        Exit step = planner.findStepTowards(
                getCreature(),
                location,
                network.getNestLocation()
        );

        if (step == null) {
            return super.getAction(location);
        }

        return new MoveActorAction(
                step.getDestination(),
                step.getName()
        );
    }
}
