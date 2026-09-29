package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.capabilities.ToolAbility;

/**
 * Hoe represents a portable tool that allows an actor to perform
 * tilling actions.
 *
 * <p>The Hoe provides the TILLING ability, which can be checked by
 * actions that require a tool capable of tilling ground.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */

public class Hoe extends Item {
    /**
     * Constructor for the Hoe class.
     *
     * <p>Creates a portable Hoe and enables the TILLING ability.</p>
     */
    public Hoe() {
        super("Hoe",'h');
        this.makePortable();
        this.enableAbility(ToolAbility.TILLING);
    }
}
