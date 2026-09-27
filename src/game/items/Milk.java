package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * Represents a bottle of Milk produced by a Cow.
 */
public class Milk extends Item {

    /**
     * Constructs a portable bottle of Milk.
     */
    public Milk() {
        super("Milk", 'M');
        this.makePortable();
    }
}
