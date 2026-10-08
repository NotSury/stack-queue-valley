package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.NestSpider;
import game.interfaces.WebNetwork;
import game.perception.EnvironmentScanner;
import game.perception.SpiderTargetTracker;

/**
 * Pursues an engaged Farmer through ordinary walking and attacks.
 * Entry temporarily reveals all owned webs and reinforces them.
 * Jumping is reserved for Pouncing.
 *
 * @author Khoo Lynn 34598901
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public class HuntingState extends BehaviourDrivenState {
    private static final int REINFORCEMENT_AMOUNT = 2;

    private final SpiderTargetTracker targetTracker;
    private final WebNetwork network;

    /**
     * Creates a Hunting state.
     *
     * @param creature the owning Spider
     * @param scanner the environmental scanner
     * @param huntingBehaviour the ordinary walking and attack behaviour
     * @param targetTracker the shared target assessment
     * @param network the owning Spider's web network
     */
    public HuntingState(
            Actor creature,
            EnvironmentScanner scanner,
            Behaviour<Actor, Action> huntingBehaviour,
            SpiderTargetTracker targetTracker,
            WebNetwork network
    ) {
        super(creature, scanner, huntingBehaviour);
        this.targetTracker = targetTracker;
        this.network = network;
    }

    /**
     * Returns this state's identifier.
     *
     * @return the Hunting identifier.
     */
    @Override
    public CreatureStateType getType(){
        return CreatureStateType.HUNTING;
    }

    /**
     * Responds to web alerts or continues a valid engaged pursuit.
     * After pursuit ends, optional weaving requires recovered health
     * and sufficient placement opportunities.
     *
     * @param location the Spider's current location
     * @return Pouncing, Hunting, Weaving or Nesting
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        if (network.getAlertLocation() != null) {
            return CreatureStateType.POUNCING;
        }

        if (targetTracker.getEngagedTarget() != null) {
            return CreatureStateType.HUNTING;
        }

        if (isRecovered()
                && targetTracker.canStartWeaving(location)) {
            return CreatureStateType.WEAVING;
        }

        return CreatureStateType.NESTING;
    }

    /**
     * Reveals all owned webs through the engaged Farmer's next turn.
     * Adds two lifetime ticks, capped at eight, without disabling traps.
     *
     * @param location the Spider's location on entry
     * @return a description of the web effects
     */
    @Override
    public String onEnter(Location location) {
        Actor farmer = targetTracker.getEngagedTarget();

        if (farmer == null) {
            return getCreature()
                    + " enters Hunting without a valid defence target.";
        }

        return network.revealAndReinforceAll(
                farmer,
                REINFORCEMENT_AMOUNT
        );
    }

    /**
     * Delegates ordinary walking and attacks while a valid target remains.
     *
     * @param location the Spider's current location
     * @return a hunting action or an idle action
     */
    @Override
    public Action getAction(Location location) {
        if (targetTracker.getEngagedTarget() == null) {
            return new DoNothingAction();
        }

        return super.getAction(location);
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
