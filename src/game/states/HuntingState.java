package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.WebNetwork;
import game.perception.EnvironmentScanner;
import game.perception.SpiderTargetTracker;

/**
 * Pursues or attacks an active Farmer. Activates nearby owned webs once when entered.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class HuntingState extends BehaviourDrivenState {
    private static final int ACTIVATION_RADIUS = 3;
    private final SpiderTargetTracker targetTracker;
    private final WebNetwork webNetwork;

    /**
     * Creates the Spider's hunting state.
     *
     * @param creature the Spider that owns this state.
     * @param scanner the environmental scanner.
     * @param huntingBehaviour the pursuit and attack behaviour.
     * @param targetTracker the shared target assessment.
     * @param webNetwork the Spider's owned webs.
     */
    public HuntingState(Actor creature, EnvironmentScanner scanner, Behaviour<Actor, Action> huntingBehaviour,
                        SpiderTargetTracker targetTracker, WebNetwork webNetwork){
        super(creature, scanner, huntingBehaviour);
        this.targetTracker = targetTracker;
        this.webNetwork = webNetwork;
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
     * <p>Selects Pouncing when jumping improves an active approach. Without an
     * active target, selects Weaving when a web gap exists and Nesting otherwise.</p>
     *
     * @param location the creature's current location
     * @return the selected state identifier.
     */
    @Override
    public CreatureStateType determineNextState(Location location){
        Actor target = targetTracker.findActiveTarget(location);

        if (target == null){
            return targetTracker.hasWebGap(location) ? CreatureStateType.WEAVING : CreatureStateType.NESTING;
        }

        Location targetLocation = location.map().locationOf(target);

        if (targetTracker.findUsefulLanding(location, targetLocation) != null){
            return CreatureStateType.POUNCING;
        }

        return CreatureStateType.HUNTING;
    }

    /**
     * Sends a vibration through nearby owned webs. The network reveals webs and
     * triggers eligible occupants.
     *
     * @param location the creature's current location
     * @return a description of the web effects.
     */
    @Override
    public String onEnter(Location location){
        return webNetwork.activeNearbyWebs(location, ACTIVATION_RADIUS);
    }

    /**
     * Selects a hunting action while a valid target remains.
     *
     * @param location the creature's current location
     * @return a hunting action or an idle action.
     */
    @Override
    public Action getAction(Location location){
        if (targetTracker.findActiveTarget(location) == null){
            return new DoNothingAction();
        }
        return  super.getAction(location);
    }
}
