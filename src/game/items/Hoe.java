package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.capabilities.Ability;

/**
 * Represents a portable hoe that can be used by the Farmer for tilling.
 */
public class Hoe extends Item {

    /**
     * Constructs a portable hoe and enables its tilling ability
     */
    public Hoe() {
        super("Hoe", 'h');
        this.makePortable();
        this.enableAbility(Ability.TILL);
    }
}
