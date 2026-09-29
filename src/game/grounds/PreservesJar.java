package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ProcessAction;
import game.interfaces.ProcessingFacility;
import game.items.PreservedProduct;
import game.items.ProcessableItem;
/**
 * PreservesJar represents a processing facility that preserves
 * processable items.
 *
 * <p>The facility converts a ProcessableItem into a PreservedProduct.
 * Processing actions are provided when an actor is adjacent to the
 * PreservesJar and is carrying processable items.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class PreservesJar  extends Ground implements ProcessingFacility {
    /**
     * Constructor for the PreservesJar class.
     *
     * <p>Creates a PreservesJar represented by the 'J' display character.</p>
     */
    public PreservesJar() {
        super('J', "Preserves Jar");
    }

    /**
     * Processes the given item using the preserving method.
     *
     * <p>A new PreservedProduct is created around the complete input
     * product, preserving all previous processing layers.</p>
     *
     * @param product the processable item to be preserved
     * @return a new preserved version of the product
     */
    @Override
    public ProcessableItem process(ProcessableItem product){
        return new PreservedProduct(product);
    }

    /**
     * Returns the name of this processing facility.
     *
     * @return the name "Preserves Jar"
     */
    @Override
    public String getFacilityName() {
        return "Preserves Jar";
    }

    /**
     * Returns processing actions available to an actor near this PreservesJar.
     *
     * <p>If the actor is adjacent to the facility, one ProcessAction is
     * provided for each ProcessableItem currently carried in the actor's
     * inventory.</p>
     *
     * @param actor the actor interacting with the facility
     * @param location the location of the PreservesJar
     * @param direction the direction of the facility relative to the actor
     * @return the available processing actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction){

        ActionList actions = new ActionList();

        if (!direction.isEmpty()){

            for (ProcessableItem product :
            actor.getInventory().getItemsAs(ProcessableItem.class)) {

                actions.add(new ProcessAction(product, this));
            }
        }
        return actions;
    }
}
