package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ProcessAction;
import game.interfaces.ProcessingFacility;
import game.items.DriedProduct;
import game.items.ProcessableItem;
/**
 * DryingRack represents a processing facility that dries processable items.
 *
 * <p>The facility converts a ProcessableItem into a DriedProduct.
 * Processing actions are provided when an actor is adjacent to the
 * DryingRack and is carrying processable items.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class DryingRack extends Ground implements ProcessingFacility {
    /**
     * Constructor for the DryingRack class.
     *
     * <p>Creates a DryingRack represented by the 'R' display character.</p>
     */
    public DryingRack() {
        super('R', "Drying Rack");
    }

    /**
     * Processes the given item using the drying method.
     *
     * <p>A new DriedProduct is created around the complete input product,
     * preserving all previous processing layers.</p>
     *
     * @param product the processable item to be dried
     * @return a new dried version of the product
     */
    @Override
    public ProcessableItem process (ProcessableItem product){
        return new DriedProduct(product);
    }

    /**
     * Returns the name of this processing facility.
     *
     * @return the name "Drying Rack"
     */
    @Override
    public String getFacilityName() {
        return "Drying Rack";
    }

    /**
     * Returns processing actions available to an actor near this DryingRack.
     *
     * <p>If the actor is adjacent to the facility, one ProcessAction is
     * provided for each ProcessableItem currently carried in the actor's
     * inventory.</p>
     *
     * @param actor the actor interacting with the facility
     * @param location the location of the DryingRack
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
