package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.ItemAbility;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.GridDistance;
import game.perception.EnvironmentScanner;

/**
 * Base class for creature states that inspect their surroundings.
 * Receives an environmental scanner through its constructor.
 * States remain stationary unless they override ordinary action selection.
 */
public abstract class EnvironmentAwareState implements CreatureState {

    /**
     * Distance used for interactions with adjacent actors.
     */
    protected static final int ADJACENT_RADIUS = 1;

    private final Actor creature;
    private final EnvironmentScanner scanner;

    /**
     * Creates a state with an owning actor and environmental information.
     *
     * @param creature the actor that owns this state
     * @param scanner the scanner used to inspect the surroundings
     */
    protected EnvironmentAwareState(Actor creature, EnvironmentScanner scanner) {
        this.creature = creature;
        this.scanner = scanner;
    }

    /**
     * Returns the actor that owns this state.
     *
     * @return the owning actor
     */
    protected final Actor getCreature() {
        return creature;
    }

    /**
     * Returns the environmental scanner available to this state.
     *
     * @return the state's scanner
     */
    protected final EnvironmentScanner getScanner() {
        return scanner;
    }

    /**
     * Checks whether an adjacent actor carries an eligible item for theft.
     * Requires a portable inventory item with an available drop action.
     * Does not execute the action or change the inventory.
     *
     * @param target the actor being assessed, or null
     * @param origin the owning creature's current location
     * @return true when the actor is adjacent and carries an eligible item
     */
    protected final boolean canStealFrom(Actor target, Location origin) {
        if (target == null) {
            return false;
        }

        Location targetLocation = origin.map().locationOf(target);

        return GridDistance.between(origin, targetLocation) == ADJACENT_RADIUS
                && findDroppablePortableItem(target) != null;
    }

    /**
     * Finds the first portable inventory item with an available drop action.
     * Uses the inventory's item ordering without changing its contents.
     *
     * @param owner the actor whose inventory is inspected
     * @return the first eligible item, or null when none is available
     */
    protected final Item findDroppablePortableItem(Actor owner) {
        for (Item item : owner.getInventory().getItems()) {
            if (item.hasAbility(ItemAbility.PORTABLE)
                    && item.getDropAction(owner) != null) {
                return item;
            }
        }

        return null;
    }

    /**
     * Selects an action that keeps the creature stationary.
     * Subclasses can override this method to provide ordinary behaviour.
     *
     * @param location the creature's current location
     * @return an action that performs no operation
     */
    @Override
    public Action getAction(Location location) {
        return new DoNothingAction();
    }
}
