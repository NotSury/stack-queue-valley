package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.BaitCarrier;
import game.interfaces.Disguisable;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

/**
 * A state in which a Cave Mimic reveals itself and confronts a Farmer.
 * Forces an adjacent Farmer to drop an eligible item on entry.
 * Delegates ordinary pursuit and attacks to an injected combat behaviour.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class AmbushState extends BehaviourDrivenState {

    private static final double ESCAPE_HEALTH_RATIO = 0.30;

    private final Disguisable disguisable;
    private final BaitCarrier baitCarrier;
    private final MovementPlanner movementPlanner;
    private final int detectionRadius;


    /**
     * Creates an Ambush state.
     * The supplied role interfaces must operate on the owning creature.
     *
     * @param creature the owning creature
     * @param scanner the environmental scanner
     * @param movementPlanner the planner used to assess escape routes
     * @param disguisable the creature's appearance operations
     * @param baitCarrier the creature's bait operations
     * @param combatBehaviour the ordinary pursuit and attack behaviour
     * @param detectionRadius the maximum Farmer detection distance
     */
    public AmbushState(
            Actor creature,
            EnvironmentScanner scanner,
            MovementPlanner movementPlanner,
            Disguisable disguisable,
            BaitCarrier baitCarrier,
            Behaviour<Actor, Action> combatBehaviour,
            int detectionRadius
    ) {
        super(creature, scanner, combatBehaviour);
        this.movementPlanner = movementPlanner;
        this.disguisable = disguisable;
        this.baitCarrier = baitCarrier;
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
     * @param location the Mimic's current location
     * @return a description of revealing the creature and any items stolen
     */
    @Override
    public String onEnter(Location location) {
        disguisable.reveal();

        Actor farmer = getScanner().findNearestActorWith(location, ADJACENT_RADIUS, ActorRole.FARMER);

        String message = getCreature() + " reveals itself.";

        if (farmer != null) {
            String theftMessage = stealDroppedItem(farmer, location.map());

            if (!theftMessage.isEmpty()) {
                message += System.lineSeparator() + theftMessage;
            }
        }

        return message;
    }

    /**
     * Returns to Disguised when no Farmer is detected and bait can be scattered.
     * Escaping requires low health, a detected Farmer and separate
     * valid initial steps for the Mimic and its decoy.
     *
     * @param location the Mimic's current location
     * @return Disguised, Escape or Ambush
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findNearestActorWith(location, detectionRadius, ActorRole.FARMER);

        if (farmer == null) {
            return baitCarrier.canScatterBait(location)
                    ? CreatureStateType.DISGUISED
                    : CreatureStateType.AMBUSH;
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

    private String stealDroppedItem(Actor farmer, GameMap map) {
        Item stolenItem = findDroppablePortableItem(farmer);

        if (stolenItem == null) {
            return "";
        }

        Action dropAction = stolenItem.getDropAction(farmer);
        dropAction.execute(farmer, map);

        Location farmerLocation = map.locationOf(farmer);

        if (farmerLocation.getItems().contains(stolenItem)) {
            farmerLocation.removeItem(stolenItem);
            getCreature().getInventory().add(stolenItem);

            return getCreature() + " forces " + farmer
                    + " to drop the " + stolenItem
                    + " and steals it.";
        }

        return "";
    }
}
