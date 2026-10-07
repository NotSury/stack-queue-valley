package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.interfaces.Hostile;
import game.states.CreatureStateType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A stateful creature that disguises itself and uses portable items as bait.
 * Provides inventory operations used by its state-entry effects.
 * State implementations control when these operations are performed.
 */
public class CaveMimic extends StatefulCreature implements Hostile {

    private static final int ADJACENT_RADIUS = 1;
    /**
     * Maximum distance used for Farmer detection and ordinary item seeking.
     */
    public static final int DETECTION_RADIUS = 3;
    private char appearance;

    /**
     * Creates a Cave Mimic with health, starting bait and a natural weapon.
     * The creature factory must register its states before activating
     * the initial state.
     *
     * @param hitPoints the positive starting and maximum health
     * @param startingBait the portable items supplied as starting bait
     * @param intrinsicWeapon the weapon used for the Mimic's natural attacks
     */
    public CaveMimic(int hitPoints, List<Item> startingBait, IntrinsicWeapon intrinsicWeapon) {
        super("Cave Mimic", 'M', hitPoints, CreatureStateType.DISGUISED);

        setIntrinsicWeapon(intrinsicWeapon);
        appearance = super.getDisplayChar();

        for (Item item : startingBait) {
            getInventory().add(item);
        }
    }

    /**
     * Scatters portable inventory items onto adjacent tiles.
     * Places at most one item on ech unoccupied tile that the Mimic can enter.
     * Uses the engine's nearby-location ordering and the inventory's item ordering.
     * Items without an eligible destination remain in the inventory.
     *
     * @param origin origin the Mimic's current location
     * @param maximumItems the non-negative maximum number of items to scatter
     * @return the number of items transferred to the ground
     */
    public int scatterBait(Location origin, int maximumItems) {
        Iterator<Item> bait = getPortableItems(getInventory().getItems()).iterator();

        int scatteredItems = 0;

        for (Location destination : origin.getNearbyLocations(ADJACENT_RADIUS)) {
            if (scatteredItems >= maximumItems || !bait.hasNext()) {
                break;
            }

            if (!destination.canActorEnter(this)) {
                continue;
            }

            Item item = bait.next();

            getInventory().remove(item);
            destination.addItem(item);
            scatteredItems++;
        }

        return scatteredItems;
    }

    /**
     * Reclaims portable ground items from adjacent tiles.
     * Transfers all adjacent portable items, including previously
     * scattered bait, into the Mimic's inventory. This immediate
     * area effect does not require movement onto the item locations.
     * Excludes the Mimic's current tile and leaves non-portable items
     * on the ground.
     *
     * @param origin the Mimic's current location
     * @return the number of items transferred into the inventory
     */
    public int reclaimAdjacentItems(Location origin) {
        int reclaimedItems = 0;

        for (Location location : origin.getNearbyLocations(ADJACENT_RADIUS)) {
            for (Item item : getPortableItems(location.getItems())) {
                location.removeItem(item);
                getInventory().add(item);
                reclaimedItems++;
            }
        }

        return reclaimedItems;
    }

    /**
     * Changes the Mimic's displayed appearance to its disguise.
     * The Mimic remains an actor with its existing health and inventory.
     *
     * @param displayChar the character used to represent the disguise
     */
    public void disguise(char displayChar) {
        appearance = displayChar;
    }

    /**
     * Restores the Mimic's normal displayed appearance.
     */
    public void reveal() {
        appearance = super.getDisplayChar();
    }

    /**
     * Returns the character representing the Mimic's current appearance.
     *
     * @return the disguised or revealed display character
     */
    @Override
    public char getDisplayChar() {
        return appearance;
    }

    /**
     * Provides the reaction required by the shared combat interface.
     * State transitions are evaluated during the Mimic's own turn.
     *
     * @param attacker the actor who attacked this creature
     * @param map the map containing the Mimic
     * @return an empty string because no additional reaction is performed
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map) {
        return "";
    }

    private List<Item> getPortableItems(List<Item> items) {
        List<Item> portableItems = new ArrayList<>();

        for (Item item : items) {
            if (item.hasAbility(ItemAbility.PORTABLE)) {
                portableItems.add(item);
            }
        }

        return portableItems;
    }
}
