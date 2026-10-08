package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.NestSpider;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.perception.EnvironmentScanner;
import game.perception.SpiderTargetTracker;

/**
 * Uses tactical jumps towards an active Farmer. Places nearby escape-route webs
 * once when entered.
 *
 * @author Khoo Lynn 34598901
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public class PouncingState extends BehaviourDrivenState {
    private static final int ENTRY_WEB_COUNT = 2;
    private static final int ARRIVAL_RADIUS = 1;
    private static final int INVESTIGATION_RADIUS = 2;

    private final SpiderTargetTracker targetTracker;
    private final WebNetwork network;

    private Location investigationLocation;
    private long investigationVersion;

    /**
     * Creates a Pouncing state.
     *
     * @param creature the owning Spider
     * @param scanner the environmental scanner
     * @param pounceBehaviour the alert-directed movement behaviour
     * @param targetTracker the shared environmental assessment
     * @param network the owning Spider's web network
     */
    public PouncingState(
            Actor creature,
            EnvironmentScanner scanner,
            Behaviour<Actor, Action> pounceBehaviour,
            SpiderTargetTracker targetTracker,
            WebNetwork network
    ) {
        super(creature, scanner, pounceBehaviour);
        this.targetTracker = targetTracker;
        this.network = network;
    }

    /**
     * Returns this state's identifier.
     *
     * @return the Pouncing identifier.
     */
    @Override
    public CreatureStateType getType(){
        return CreatureStateType.POUNCING;
    }

    /**
     * Redirects newer alerts, investigates after arrival, and concludes
     * alerts that cannot be approached safely.
     * Only eligible Farmers inside the pursuit boundary start Hunting.
     *
     * @param location the Spider's current location
     * @return Pouncing, Hunting, Weaving or Nesting
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Location latestAlert = network.getAlertLocation();

        if (latestAlert == null) {
            investigationLocation = null;
            return selectRestOrWeaving(location);
        }

        if (network.getAlertVersion() != investigationVersion) {
            updateInvestigation();
            return CreatureStateType.POUNCING;
        }

        if (investigationLocation == null
                || investigationLocation.map() != location.map()) {
            clearInvestigation();
            return selectRestOrWeaving(location);
        }

        if (GridDistance.between(
                location,
                investigationLocation
        ) <= ARRIVAL_RADIUS) {
            Actor farmer = targetTracker.findFarmerNear(
                    investigationLocation,
                    INVESTIGATION_RADIUS
            );

            clearInvestigation();

            if (targetTracker.isValidHuntingTarget(farmer)) {
                targetTracker.engage(farmer);
                return CreatureStateType.HUNTING;
            }

            return selectRestOrWeaving(location);
        }

        if (!targetTracker.canInvestigateAlert(location)) {
            clearInvestigation();
            return selectRestOrWeaving(location);
        }

        return CreatureStateType.POUNCING;
    }

    /**
     * Records the current alert and establishes departure traps.
     * Prefers eligible tiles behind the direction towards the alert.
     *
     * @param location the Spider's location on entry
     * @return a description of the departure trap effect
     */
    @Override
    public String onEnter(Location location) {
        updateInvestigation();

        int placed = network.placeEntryWebs(
                location,
                ENTRY_WEB_COUNT,
                investigationLocation
        );

        return getCreature() + " establishes "
                + placed + " departure web(s) before investigating.";
    }

    /**
     * Delegates movement towards the current alert.
     * Already-arrived investigations wait for the next transition check.
     *
     * @param location the Spider's current location
     * @return a pouncing, walking or idle action
     */
    @Override
    public Action getAction(Location location) {
        Location alert = network.getAlertLocation();

        if (alert == null
                || alert.map() != location.map()
                || GridDistance.between(location, alert)
                <= ARRIVAL_RADIUS) {
            return new DoNothingAction();
        }

        return super.getAction(location);
    }

    private void updateInvestigation() {
        investigationLocation = network.getAlertLocation();
        investigationVersion = network.getAlertVersion();
    }

    private void clearInvestigation() {
        network.clearAlert(investigationVersion);
        investigationLocation = null;
    }

    private CreatureStateType selectRestOrWeaving(Location location) {
        if (isRecovered()
                && targetTracker.canStartWeaving(location)) {
            return CreatureStateType.WEAVING;
        }

        return CreatureStateType.NESTING;
    }

    private boolean isRecovered() {
        int health = getCreature().getStatistic(
                ActorStatistics.HEALTH
        );

        int maximumHealth = getCreature().getMaximumStatistic(
                ActorStatistics.HEALTH
        );

        return health * 100
                >= maximumHealth * NestSpider.RECOVERED_THRESHOLD;
    }
}
