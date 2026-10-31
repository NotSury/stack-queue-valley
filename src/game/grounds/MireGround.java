package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Swamp created by the Mire curse, a dead Slime and a Zombie Slime (REQ3).
 * Nobody can walk onto it, and any item lying on it sinks and is lost. The swamp
 * dries up after a few turns and the original ground returns.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class MireGround extends TemporaryGround {

    /** How many turns the swamp lasts. */
    private static final int DURATION = 4;

    /**
     * Creates swamp on top of the existing ground.
     *
     * @param previous the ground to restore when the swamp dries up
     */
    public MireGround(Ground previous) {
        super('=', "Mire", previous, DURATION);
    }

    /**
     * Swamp cannot be walked onto.
     *
     * @param actor the actor trying to enter
     * @return always false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Swallows every item lying on the swamp, then counts down as normal.
     *
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        List<Item> items = new ArrayList<>(location.getItems());
        for (Item item : items) {
            location.removeItem(item);
        }
        super.tick(location);
    }
}