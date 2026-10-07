package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CaveMimic;
import game.navigation.EscapePlan;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 A state in which a Cave Mimic reveals itself and confronts a Farmer.
 * Forces an adjacent Farmer to drop an eligible item on entry.
 * Delegates ordinary pursuit and attacks to an injected combat behaviour.
 */
public class AmbushState extends BehaviourDrivenState {

    private static final int ADJACENT_RADIUS = 1;
    private static final double ESCAPE_HEATH_RATIO = 0.30;

    private final CaveMimic mimic;
    private final MovementPlanner movementPlanner;

    /**
     * Creates an ambush state with combat and escape-planning support.
     *
     * @param mimic the Mimic that owns this state
     * @param scanner the scanner used to detect Farmers
     * @param movementPlanner the planner used to check escape opportunities
     * @param combatBehaviour the behaviour used for ordinary pursuit and attacks
     */
    public AmbushState(CaveMimic mimic, EnvironmentScanner scanner, MovementPlanner movementPlanner, Behaviour<Actor, Action> combatBehaviour) {
        super(mimic, scanner, combatBehaviour);
        this.mimic = mimic;
        this.movementPlanner = movementPlanner;
    }

    /**
     * Returns the identifier for this state.
     *
     * @return the Ambush state identifier
     */
    @Override
    public CreatureStateType getType() {
        return CreatureStateType.AMBUSH;
    }

    /**
     * Reveals the Mimic and steals one item from an adjacent Farmer.
     * The Farmer first drops an eligible portable item through its
     * engine drop action. The Mimic then takes that same item from
     * the Farmer's tile without moving.
     *
     * @param location the Mimic's current location.
     */
    @Override
    public void onEnter(Location location) {
        mimic.reveal();

        Actor farmer = getScanner().findFarmer(location, ADJACENT_RADIUS);

        if (farmer != null) {
            stealDroppedItem(farmer, location.map());
        }
    }

    /**
     * Determines whether to escape, disguise or continue confronting the Farmer.
     * Escaping requires low health, a detected Farmer and two improving
     * movement steps with different destinations.
     * Stores the complete plan before selecting Escape.
     *
     * @param location the Mimic's current location
     * @return Escape, Disguised or Ambush according to the surroundings
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findFarmer(location, CaveMimic.DETECTION_RADIUS);

        if (farmer == null) {
            if (mimic.hasAbility(ItemAbility.PORTABLE)) {
                return CreatureStateType.DISGUISED;
            }

            return CreatureStateType.AMBUSH;
        }

        double escapeHealthThreshold =
                mimic.getMaximumStatistic(ActorStatistics.HEALTH)
                        * ESCAPE_HEATH_RATIO;

        if (mimic.getStatistic(ActorStatistics.HEALTH)
                <= escapeHealthThreshold) {

            Location threatLocation = location.map().locationOf(farmer);

            Exit mimicStep = movementPlanner.findStepAway(mimic, location, threatLocation);

            if (mimicStep != null) {
                Exit decoyStep = movementPlanner.findStepAway(mimic, location, threatLocation, mimicStep);

                if (decoyStep != null) {
                    mimic.prepareEscape(new EscapePlan(mimicStep, decoyStep));

                    return CreatureStateType.ESCAPE;
                }
            }
        }

        return CreatureStateType.AMBUSH;
    }

    private void stealDroppedItem(Actor farmer, GameMap map) {
        Item stolenItem = null;
        Action dropAction = null;

        for (Item item : farmer.getInventory().getItems()) {
            if (!item.hasAbility(ItemAbility.PORTABLE)) {
                continue;
            }

            Action candidateDrop = item.getDropAction(farmer);

            if (candidateDrop != null) {
                stolenItem = item;
                dropAction = candidateDrop;
                break;
            }
        }

        if (dropAction == null) {
            return;
        }

        dropAction.execute(farmer, map);

        Location farmerLocation = map.locationOf(farmer);

        if (farmerLocation.getItems().contains(stolenItem)) {
            farmerLocation.removeItem(stolenItem);
            mimic.getInventory().add(stolenItem);

            System.out.println(mimic + " forces " + farmer + " to drop the " + stolenItem + " and steals it.");
        }
    }
}
