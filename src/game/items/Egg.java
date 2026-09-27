package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * Represents an Egg produced by a Chicken.
 */
public class Egg extends Item {

    /**
     * Constructs a portable Egg.
     */
    public Egg() {
        super("Egg", '0');
        this.makePortable();
    }
}
