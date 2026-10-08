package game.states;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.HealAction;
import game.actors.NestSpider;
import game.actors.Spiderling;
import game.interfaces.SpiderlingFactory;
import game.interfaces.WebNetwork;
import game.navigation.MovementSafety;
import game.navigation.WebPlanner;
import game.perception.EnvironmentScanner;
import game.perception.SpiderAwareness;
import game.perception.SpiderTargetTracker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Returns the Spider to its fixed nest and supports recovery.
 * Entry creates one Spiderling when a safe spawn tile exists
 * and maintains two guard webs around the nest.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public class NestingState extends EnvironmentAwareState {

    private static final int REQUIRED_QUIET_TURNS = 3;
    private static final int GUARD_WEB_COUNT = 2;
    private static final int HEALING_AMOUNT = 3;

    private final WebNetwork network;
    private final WebPlanner planner;
    private final SpiderTargetTracker targetTracker;
    private final SpiderlingFactory spiderlingFactory;

    private int quietTurns;

    /**
     * Creates a Nesting state with injected environmental collaborators.
     *
     * @param creature the owning Spider
     * @param scanner the environmental scanner
     * @param network the owning Spider's web network
     * @param planner the safe walking planner
     * @param targetTracker the shared threat assessment
     * @param spiderlingFactory the Spiderling construction collaborator
     */
    public NestingState(
            Actor creature,
            EnvironmentScanner scanner,
            WebNetwork network,
            WebPlanner planner,
            SpiderTargetTracker targetTracker,
            SpiderlingFactory spiderlingFactory
    ) {
        super(creature, scanner);
        this.network = network;
        this.planner = planner;
        this.targetTracker = targetTracker;
        this.spiderlingFactory = spiderlingFactory;
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
     * Creates a Spiderling beside the parent, maintains nest guards
     * and resets the quiet period.
     *
     * @param location the Spider's location on entry
     * @return a description of the entry effects
     */
    @Override
    public String onEnter(Location location) {
        quietTurns = 0;

        String spawnResult = spawnSpiderling(location);
        int maintained = network.maintainGuardWebs(GUARD_WEB_COUNT);

        return spawnResult + "\n" + getCreature()
                + " maintains " + maintained
                + " guard web(s) around its nest.";
    }

    /**
     * Prioritizes web alerts, then direct nest intrusion.
     * Optional weaving requires recovery and three quiet turns at home.
     *
     * @param location the Spider's current location
     * @return Pouncing, Hunting, Weaving or Nesting
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        if (network.getAlertLocation() != null) {
            quietTurns = 0;
            return CreatureStateType.POUNCING;
        }

        Actor intruder = targetTracker.findNestIntruder();

        if (intruder != null) {
            quietTurns = 0;
            targetTracker.engage(intruder);
            return CreatureStateType.HUNTING;
        }

        if (location != network.getNestLocation()) {
            quietTurns = 0;
            return CreatureStateType.NESTING;
        }

        quietTurns++;

        if (quietTurns >= REQUIRED_QUIET_TURNS
                && isRecovered()
                && targetTracker.canStartWeaving(location)) {
            return CreatureStateType.WEAVING;
        }

        return CreatureStateType.NESTING;
    }

    /**
     * Heals while at home or selects a safe walking step towards home.
     *
     * @param location the Spider's current location
     * @return a healing, walking or idle action
     */
    @Override
    public Action getAction(Location location) {
        if (location == network.getNestLocation()) {
            int health = getCreature().getStatistic(
                    ActorStatistics.HEALTH
            );

            int maximumHealth = getCreature().getMaximumStatistic(
                    ActorStatistics.HEALTH
            );

            if (health < maximumHealth) {
                return new HealAction(HEALING_AMOUNT);
            }

            return super.getAction(location);
        }

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

    private boolean isRecovered() {
        int health = getCreature().getStatistic(ActorStatistics.HEALTH);

        int maximumHealth = getCreature().getMaximumStatistic(ActorStatistics.HEALTH);

        return health * 100 >= maximumHealth * NestSpider.RECOVERED_THRESHOLD;
    }

    private String spawnSpiderling(Location origin) {
        Spiderling spiderling = spiderlingFactory.createSpiderling();

        List<Location> candidates = new ArrayList<>(
                origin.getNearbyLocations(ADJACENT_RADIUS)
        );

        candidates.sort(
                Comparator.comparingInt(Location::y)
                        .thenComparingInt(Location::x)
        );

        for (Location candidate : candidates) {
            if (candidate == network.getNestLocation()
                    || !candidate.canActorEnter(spiderling)
                    || !MovementSafety.isSafe(candidate)) {
                continue;
            }

            try {
                origin.map().addActor(spiderling, candidate);

                return getCreature()
                        + " creates a Spiderling at ("
                        + candidate.x() + ", "
                        + candidate.y() + ").";
            } catch (GameEngineException exception) {
                return getCreature()
                        + " cannot place its Spiderling: "
                        + exception.getMessage();
            }
        }

        return getCreature()
                + " has no safe adjacent tile for a Spiderling.";
    }
}
