package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * Egg represents an item that can be produced by a Chicken after feeding.
 *
 * <p>An Egg is a portable item that can be carried in an actor's
 * inventory or dropped onto the game map.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Egg extends Item {
    /**
     * Constructor for the Egg class.
     *
     * <p>Creates a portable Egg with the display character '0'.</p>
     */
    public Egg() {
        super("Egg",'0');
        this.makePortable();
    }
}
