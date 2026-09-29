package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.interfaces.ProcessingFacility;
import game.items.ProcessableItem;
/**
 * ProcessAction represents an action that processes a ProcessableItem
 * using a ProcessingFacility.
 *
 * <p>The selected facility creates a new processed product from the
 * original item. The original item is removed from the actor's inventory
 * and replaced with the newly created processed product.</p>
 *
 * <p>This action depends on the ProcessingFacility abstraction rather
 * than a specific processing facility, allowing different processing
 * methods to use the same action.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class ProcessAction extends Action {
    /**
     * The processable item selected for processing.
     */
    private final ProcessableItem product;
    /**
     * The facility used to process the selected product.
     */
    private final ProcessingFacility facility;
    /**
     * Constructor for the ProcessAction class.
     *
     * @param product the processable item to be processed
     * @param facility the processing facility used to process the item
     */
    public ProcessAction(ProcessableItem product, ProcessingFacility facility) {
        this.product = product;
        this.facility = facility;
    }

    /**
     * Executes the processing action.
     *
     * <p>The selected facility creates a new processed product from the
     * original item. The original product is then removed from the actor's
     * inventory and replaced with the newly created product.</p>
     *
     * @param actor the actor performing the processing action
     * @param map the game map where the action is performed
     * @return a description of the processing result
     */
    @Override
    public String execute(Actor actor, GameMap map){

        ProcessableItem processedProduct = facility.process(product);

        actor.getInventory().remove(product);
        actor.getInventory().add(processedProduct);

        return actor + " processes " + product.getProductName() +
                " using " + facility.getFacilityName() + " into " +
                processedProduct.getProductName() + ".";
    }

    /**
     * Returns the description of this action for display in the action menu.
     *
     * @param actor the actor performing the action
     * @return the menu description of the processing action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " processes " + product.getProductName() +
                " using " + facility.getFacilityName();
    }
}
