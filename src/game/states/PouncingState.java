package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.WebNetwork;
import game.navigation.GridDistance;
import game.perception.EnvironmentScanner;
import game.perception.SpiderTargetTracker;

/**
 * Uses tactical jumps towards an active Farmer. Places nearby escape-route webs
 * once when entered.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class PouncingState extends BehaviourDrivenState {
    private static final int ENTRY_WEB_LIMIT = 2;
    private static final int WEB_RADIUS = 3;

    private final SpiderTargetTracker targetTracker;
    private final WebNetwork webNetwork;

    /**
     * Creates the Spider's pouncing state.
     *
     * @param creature the Spider that owns this state.
     * @param scanner the environmental scanner.
     * @param pounceBehaviour the tactical jumping behaviour.
     * @param targetTracker the shared target assessment.
     * @param webNetwork the Spider's owned webs.
     */
    public PouncingState(Actor creature, EnvironmentScanner scanner, Behaviour<Actor, Action> pounceBehaviour,
                         SpiderTargetTracker targetTracker, WebNetwork webNetwork){
        super(creature, scanner, pounceBehaviour);
        this.targetTracker = targetTracker;
        this.webNetwork = webNetwork;
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
     * <p>Returns to Hunting when walking can approach the Farmer and no useful jump
     * remains. Without an active target, selects Weaving when a web gap exists amd
     * Nesting otherwise.</p>
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

        boolean usefulJump = targetTracker.findUsefulLanding(location, targetLocation) != null;

        if (!usefulJump && targetTracker.canWalkToTarget(location, targetLocation)){
            return CreatureStateType.HUNTING;
        }

        return CreatureStateType.POUNCING;
    }

    /**
     * Places up to two webs around the active Farmer. Only eligible locations within the
     * nest's web area are used.
     *
     * @param location the creature's current location
     * @return a description of placed webs or an empty string.
     */
    @Override
    public String onEnter(Location location){
        Actor target = targetTracker.findActiveTarget(location);

        if (target == null){
            return "";
        }

        GameMap map = location.map();
        Location targetLocation = map.locationOf(target);
        Location nest = webNetwork.getNestLocation();
        int placed = 0;

        for (int y = targetLocation.y() - 1; y <= targetLocation.y() + 1 && placed < ENTRY_WEB_LIMIT; y++) {
            for (int x = targetLocation.x() - 1; x <= targetLocation.x() + 1 && placed < ENTRY_WEB_LIMIT; x++) {
                if (!map.getXRange().contains(x) || !map.getYRange().contains(y)) {
                    continue;
                }

                Location candidate = map.at(x, y);

                if (candidate == targetLocation || candidate == nest
                        || GridDistance.between(nest, candidate) > WEB_RADIUS) {
                    continue;
                }

                if (webNetwork.placeAt(candidate)) {
                    placed++;
                }
            }
        }

        if (placed == 0) {
            return "";
        }

        return getCreature() + " spreads " + placed + " web trap(s) around " + target + ".";
    }

    /**
     * Selects a pounce while an active target remains. Falls back to an idle action
     * when no jump is selected.
     *
     * @param location the creature's current location
     * @return a jump action or an idle action.
     */
    @Override
    public Action getAction(Location location){
        if (targetTracker.findActiveTarget(location) == null){
            return new DoNothingAction();
        }

        return super.getAction(location);
    }
}
