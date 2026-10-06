package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BreakingAction;
import game.interfaces.Breakable;

/**
 * Base class for impassable grounds that can be broken.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public abstract class BreakableGround extends Ground implements Breakable {

    /**
     * Creates a breakable ground.
     *
     * @param displayChar character used to display the ground.
     * @param name name of the ground.
     */
    protected BreakableGround(char displayChar, String name){
        super(displayChar, name);
    }

    /**
     * Prevents actors from entering this ground.
     *
     * @param actor actor attempting to enter.
     * @return false.
     */
    @Override
    public boolean canActorEnter(Actor actor){
        return false;
    }

    /**
     * Provides a BreakingAction for this ground.
     *
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction){
        ActionList actions = new ActionList();
        actions.add(new BreakingAction(this, location));
        return actions;
    }
}
