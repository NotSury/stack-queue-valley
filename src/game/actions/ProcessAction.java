package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.ProcessingMethod;
import game.items.ProcessableProduct;

/**
 * Represents an action that processes a selected product using
 * a processing method.
 */
public class ProcessAction extends Action {

    private final ProcessableProduct product;
    private final ProcessingMethod processingMethod;

    /**
     * Constructs a processing action.
     *
     * @param product the product selected for processing
     * @param processingMethod the method used to process the product
     */
    public ProcessAction(ProcessableProduct product, ProcessingMethod processingMethod) {
        this.product = product;
        this.processingMethod = processingMethod;
    }

    /**
     * Processes the selected product.
     *
     * @param actor the actor performing the processing
     * @param map the map containing the actor
     * @return a description of the processing result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        ProcessableProduct processedProduct = processingMethod.process(product);

        actor.getInventory().remove(product);
        actor.getInventory().add(processedProduct);

        return actor + " processes " + product + " using " + processingMethod + " into " + processedProduct + ".";
    }

    /**
     * Describes this processing action in the menu.
     *
     * @param actor the actor performing the action
     * @return the menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " processes " + product + " using " + processingMethod;
    }
}
