package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.CaveMimic;
import game.navigation.GridDistance;
import game.perception.EnvironmentScanner;

/**
 * A state in which a Cave Mimic reclaims nearby items and seeks new loot.
 * Records its inventory size after entry-time reclamation so reclaimed
 * items do not count towards the collection goal.
 * Progress tracking assumes ordinary pickups are the only operations
 * changing the Mimic's inventory while it remains in this state.
 */
public class LootingState extends BehaviourDrivenState {

    private static final int COLLECTION_GOAL = 2;

    private final CaveMimic mimic;

    private int initialInventorySize;

    /**
     * Creates a looting state with an item-seeking behaviour.
     *
     * @param mimic the Mimic that owns this state
     * @param scanner the scanner used to inspect the surroundings
     * @param itemSeekingBehaviour the behaviour used to approach and collect loot
     */
    public LootingState(CaveMimic mimic, EnvironmentScanner scanner, Behaviour<Actor, Action> itemSeekingBehaviour) {
        super(mimic, scanner, itemSeekingBehaviour);
        this.mimic = mimic;
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
     * Reveals the Mimic, reclaims adjacent items and records the
     * inventory baseline for this visit.
     *
     * @param location the Mimic's current location
     */
    @Override
    public void onEnter(Location location) {
        mimic.reveal();
        mimic.reclaimAdjacentItems(location);
        initialInventorySize = mimic.getInventory().getItems().size();
    }

    /**
     * Determines whether to ambush, disguise or continue looting.
     * An adjacent Farmer carrying a portable item takes priority.
     * Returning to Disguised requires no detected Farmer and portable
     * bait in the Mimic's inventory.
     *
     * @param location the creature's current location
     * @return Ambush, Disguised or Looting according to the surroundings
     */
    @Override
    public CreatureStateType determineNextState(Location location) {
        Actor farmer = getScanner().findFarmer(
                location, CaveMimic.DETECTION_RADIUS);

        if (farmer != null) {
            Location farmerLocation = location.map().locationOf(farmer);

            if (GridDistance.between(location, farmerLocation) == 1
                    && farmer.hasAbility(ItemAbility.PORTABLE)) {
                return CreatureStateType.AMBUSH;
            }

            return CreatureStateType.LOOTING;
        }

        if (mimic.hasAbility(ItemAbility.PORTABLE)) {
            int newItemsCollected =
                    mimic.getInventory().getItems().size() - initialInventorySize;

            if (newItemsCollected >= COLLECTION_GOAL) {
                return CreatureStateType.DISGUISED;
            }

            Location loot = getScanner().findNearestPortableItemLocation(
                    mimic, location, 0, CaveMimic.DETECTION_RADIUS);

            if (loot == null) {
                return CreatureStateType.DISGUISED;
            }
        }

        return CreatureStateType.LOOTING;
    }
}
