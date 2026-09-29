package game.items;

import edu.monash.fit2099.engine.items.Item;
/**
 * Milk represents an item that is produced when a Cow is fed.
 *
 * <p>Milk is portable and can therefore be carried in an actor's
 * inventory or dropped onto the game map.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Milk extends Item {
    /**
     * Constructor for the Milk class.
     *
     * <p>Creates a portable Milk item with the display character 'M'.</p>
     */
    public Milk() {
        super("Milk",'M');
        this.makePortable();
    }
}
