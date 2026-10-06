package game.items;

import edu.monash.fit2099.engine.items.Item;

/**
 * A portable Copper Ore obtained by breaking a Copper Node.
 *
 * @author Khoo Lynn
 * @version 1.0.0
 */
public class CopperOre extends Item {

    /**
     * Creates a portable Copper Ore.
     */
    public CopperOre(){
        super("Copper Ore", 'O');
        this.makePortable();
    }
}
