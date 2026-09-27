package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ProcessAction;
import game.capabilities.ProcessingMethod;
import game.items.ProcessableProduct;

/**
 * Represents a processing facility that can apply a processing method
 * to processable products carried by an actor.
 * A processing facility is a ground type that provides processing actions
 * for all carried products that support the ProcessableProduct abstraction.
 */
public abstract class ProcessingFacility extends Ground implements ProcessingMethod {

    /**
     * Constructs a processing facility
     *
     * @param displayChar the character representing this facility
     * @param name the name of this facility
     */
    protected ProcessingFacility(char displayChar, String name) {
        super(displayChar, name);
    }

    /**
     * Returns processing actions for all processable products carried by
     * the interacting actor.
     *
     * @param actor the actor interacting with this facility
     * @param location the location of this facility
     * @param direction the direction of this facility from the actor
     * @return the available processing actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        for (ProcessableProduct product : actor.getInventory().getItemsAs(ProcessableProduct.class)) {
            actions.add(new ProcessAction(product, this));
        }

        return actions;
    }
}
