package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.NestSpider;
import game.interfaces.WebNetwork;
import game.perception.EnvironmentScanner;
import game.perception.SpiderTargetTracker;

/**
 * Expands the web network through strategic ordinary placement.
 * Entry places one adjacent web and reinforces nearby owned webs.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public class WeavingState extends BehaviourDrivenState {

    private static final int ENTRY_WEB_COUNT = 1;
    private static final int REINFORCEMENT_RADIUS = 2;
    private static final int REINFORCEMENT_AMOUNT = 3;

    private final WebNetwork network;
    private final SpiderTargetTracker targetTracker;

    /**
     * Creates a Weaving state.
     *
     * @param creature the owning Spider
     * @param scanner the environmental scanner
     * @param network the owning Spider's web network
     * @param targetTracker the shared environmental assessment
     * @param weavingBehaviour the strategic ordinary placement behaviour
     */
    public WeavingState(
            Actor creature,
            EnvironmentScanner scanner,
            WebNetwork network,
            SpiderTargetTracker targetTracker,
            Behaviour<Actor, Action> weavingBehaviour
    ) {
        super(creature, scanner, weavingBehaviour);
        this.network = network;
        this.targetTracker = targetTracker;
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
     * Places one adjacent entry web and reinforces nearby owned webs.
     * A new web already starts at the maximum eight-tick lifetime.
     *
     * @param location the Spider's location on entry
     * @return a description of placement and reinforcement
     */
    @Override
    public String onEnter(Location location) {
        int placed = network.placeEntryWebs(
                location,
                ENTRY_WEB_COUNT,
                null
        );

        int reinforced = network.reinforceNearby(
                location,
                REINFORCEMENT_RADIUS,
                REINFORCEMENT_AMOUNT
        );

        return getCreature() + " places " + placed
                + " entry web(s) and reinforces "
                + reinforced + " nearby web(s).";
    }

    /**
     * Prioritises alerts and nest intrusion before recovery and capacity.
     *
     * @param location the Spider's current location
     * @return Pouncing, Hunting, Nesting or Weaving
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        if (network.getAlertLocation() != null) {
            return CreatureStateType.POUNCING;
        }

        Actor intruder = targetTracker.findNestIntruder();

        if (intruder != null) {
            targetTracker.engage(intruder);
            return CreatureStateType.HUNTING;
        }

        if (requiresRecovery()
                || !network.hasCapacityFor(1)
                || !targetTracker.hasWebGap(location)) {
            return CreatureStateType.NESTING;
        }

        return CreatureStateType.WEAVING;
    }

    private boolean requiresRecovery() {
        int health = getCreature().getStatistic(
                ActorStatistics.HEALTH
        );

        int maximumHealth = getCreature().getMaximumStatistic(
                ActorStatistics.HEALTH
        );

        return health * 100
                <= maximumHealth * NestSpider.RECOVERY_THRESHOLD;
    }

}
