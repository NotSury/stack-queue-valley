package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.ActorRole;
import game.interfaces.BaitCarrier;
import game.interfaces.Disguisable;
import game.navigation.GridDistance;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A state in which a Cave Mimic reclaims current and adjacent items,
 * pulls distant loot towards itself and seeks items during ordinary turns.
 * Records its inventory size after entry-time reclamation so those
 * acquisitions do not count towards the ordinary collection goal.
 * Progress tracking assumes ordinary pickups are the only operations
 * changing the inventory while the Mimic remains in this state.
 */
public class LootingState extends BehaviourDrivenState {

    private static final int COLLECTION_GOAL = 2;
    private static final int MAXIMUM_PULL_ITEMS = 2;
    private static final int MINIMUM_PULL_DISTANCE = 2;

    private final Disguisable disguisable;
    private final BaitCarrier baitCarrier;
    private final int detectionRadius;

    private final MovementPlanner movementPlanner;

    private int initialInventorySize;

    /**
     * Creates a looting state with appearance, bait and movement support.
     * The supplied role interfaces must operate on the owning creature.
     *
     * @param creature the actor that owns this state
     * @param scanner the scanner used to detect Farmers and portable items
     * @param movementPlanner the planner used to pull reachable items
     * @param disguisable the owning creature's appearance operations
     * @param baitCarrier the owning creature's bait operations
     * @param itemSeekingBehaviour the behaviour used during ordinary looting turns
     * @param detectionRadius the maximum distance used for environmental scans
     */
    public LootingState(
            Actor creature,
            EnvironmentScanner scanner,
            MovementPlanner movementPlanner,
            Disguisable disguisable,
            BaitCarrier baitCarrier,
            Behaviour<Actor, Action> itemSeekingBehaviour,
            int detectionRadius
    ) {
        super(creature, scanner, itemSeekingBehaviour);
        this.movementPlanner = movementPlanner;
        this.disguisable = disguisable;
        this.baitCarrier = baitCarrier;
        this.detectionRadius = detectionRadius;
    }

    /**
     * Returns the identifier for this state.
     *
     * @return the Looting state identifier
     */
    @Override
    public CreatureStateType getType() {
        return CreatureStateType.LOOTING;
    }

    /**
     * Reveals the creature, reclaims current and adjacent portable items,
     * pulls distant loot and records the inventory baseline afterwards.
     * Entry-time reclamation does not count towards ordinary collection.
     *
     * @param location the creature's current location
     * @return a description of reclaimed and pulled items
     */
    @Override
    public String onEnter(Location location) {
        disguisable.reveal();
        int reclaimedItems = baitCarrier.reclaimNearbyItems(location);
        String pullMessage = pullNearbyItems(location);

        initialInventorySize = getCreature().getInventory().getItems().size();

        String message = getCreature()
                + " reveals itself and reclaims "
                + reclaimedItems
                + " portable item(s) from its current and adjacent tiles.";

        if (!pullMessage.isEmpty()) {
            message += System.lineSeparator() + pullMessage;
        }

        return message;
    }

    /**
     * Determines whether to ambush, disguise or continue looting.
     * An adjacent Farmer carrying a portable item takes priority.
     * With no detected Farmer, returns to Disguised when the collection
     * goal is reached or no reachable portable loot remains.
     *
     * @param location the creature's current location
     * @return Ambush, Disguised or Looting according to the surroundings
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findNearestActorWith(
                location, detectionRadius, ActorRole.FARMER);

        if (farmer != null) {
            if (canStealFrom(farmer, location)) {
                return CreatureStateType.AMBUSH;
            }

            return CreatureStateType.LOOTING;
        }

        int newItemsCollected =
                getCreature().getInventory().getItems().size() - initialInventorySize;

        if (newItemsCollected >= COLLECTION_GOAL) {
            return CreatureStateType.DISGUISED;
        }

        Location loot = getScanner().findNearestPortableItemLocation(
                getCreature(), location, 0, detectionRadius);

        if (loot == null) {
            return CreatureStateType.DISGUISED;
        }

        return CreatureStateType.LOOTING;
    }

    private String pullNearbyItems(Location location) {
        StringBuilder messages = new StringBuilder();
        Map<Item, Location> sources = new LinkedHashMap<>();
        Map<Item, Location> destinations = new HashMap<>();

        for (Location source
                : location.getNearbyLocations(detectionRadius)) {

            if (sources.size() >= MAXIMUM_PULL_ITEMS) {
                break;
            }

            if (GridDistance.between(location, source) < MINIMUM_PULL_DISTANCE
                    || !source.canActorEnter(getCreature())
                    || source.getItems().isEmpty()) {
                continue;
            }

            Exit step = movementPlanner.findStepTowards(
                    getCreature(), source, location);

            if (step == null) {
                continue;
            }

            for (Item item : source.getItems()) {
                if (!item.hasAbility(ItemAbility.PORTABLE)) {
                    continue;
                }

                sources.put(item, source);
                destinations.put(item, step.getDestination());

                if (sources.size() >= MAXIMUM_PULL_ITEMS) {
                    break;
                }
            }
        }

        for (Map.Entry<Item, Location> entry : sources.entrySet()) {
            Item item = entry.getKey();
            Location source = entry.getValue();
            Location destination = destinations.get(item);

            source.removeItem(item);
            destination.addItem(item);

            messages.append(
                    getCreature() + " pulls " + item
                            + " from (" + source.x() + ", " + source.y() + ")"
                            + " to (" + destination.x() + ", " + destination.y() + ")"
            ).append(System.lineSeparator());
        }

        return messages.toString().stripTrailing();
    }
}
