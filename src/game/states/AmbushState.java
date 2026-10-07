package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.Disguisable;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 A state in which a Cave Mimic reveals itself and confronts a Farmer.
 * Forces an adjacent Farmer to drop an eligible item on entry.
 * Delegates ordinary pursuit and attacks to an injected combat behaviour.
 */
public class AmbushState extends BehaviourDrivenState {

    private static final double ESCAPE_HEALTH_RATIO = 0.30;

    private final Disguisable disguisable;
    private final int detectionRadius;

    private final MovementPlanner movementPlanner;

    /**
     * Creates an ambush state with appearance, combat and escape-planning support.
     * The supplied appearance operations must operate on the owning creature.
     *
     * @param creature the actor that owns this state
     * @param scanner the scanner used to detect Farmers
     * @param movementPlanner the planner used to check escape opportunities
     * @param disguisable the owning creature's appearance operations
     * @param combatBehaviour the behaviour used for ordinary pursuit and attacks
     * @param detectionRadius the maximum distance used to detect Farmers
     */
    public AmbushState(
            Actor creature,
            EnvironmentScanner scanner,
            MovementPlanner movementPlanner,
            Disguisable disguisable,
            Behaviour<Actor, Action> combatBehaviour,
            int detectionRadius
    ) {
        super(creature, scanner, combatBehaviour);
        this.movementPlanner = movementPlanner;
        this.disguisable = disguisable;
        this.detectionRadius = detectionRadius;
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
        disguisable.reveal();

        Actor farmer = getScanner().findNearestActorWith(location, ADJACENT_RADIUS, ActorRole.FARMER);

        if (farmer != null) {
            stealDroppedItem(farmer, location.map());
        }
    }

    /**
     * Determines whether to escape, disguise or continue confronting the Farmer.
     * Escaping requires low health, a detected Farmer and separate valid
     * escape steps for the Mimic and its decoy.
     *
     * @param location the Mimic's current location
     * @return the next state according to health and the surroundings
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findNearestActorWith(location, detectionRadius, ActorRole.FARMER);

        if (farmer == null) {
            return CreatureStateType.DISGUISED;
        }

        double escapeHealthThreshold =
                getCreature().getMaximumStatistic(ActorStatistics.HEALTH)
                        * ESCAPE_HEALTH_RATIO;

        if (getCreature().getStatistic(ActorStatistics.HEALTH)
                <= escapeHealthThreshold) {

            Location threatLocation = location.map().locationOf(farmer);

            if (movementPlanner.planEscape(getCreature(), location, threatLocation) != null) {
                return CreatureStateType.ESCAPE;
            }
        }

        return CreatureStateType.AMBUSH;
    }

    private void stealDroppedItem(Actor farmer, GameMap map) {
        Item stolenItem = findDroppablePortableItem(farmer);

        if (stolenItem == null) {
            return;
        }

        Action dropAction = stolenItem.getDropAction(farmer);
        dropAction.execute(farmer, map);

        Location farmerLocation = map.locationOf(farmer);

        if (farmerLocation.getItems().contains(stolenItem)) {
            farmerLocation.removeItem(stolenItem);
            getCreature().getInventory().add(stolenItem);

            System.out.println(
                    getCreature() + " forces " + farmer
                            + " to drop the " + stolenItem
                            + " and steals it."
            );
        }
    }
}
