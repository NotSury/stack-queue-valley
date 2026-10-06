package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.capabilities.ToolAbility;

/**
 * A portable tool that allows an actor to break breakable objects.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class Pickaxe extends Item {

    /**
     * Creates a portable Pickaxe with the BREAKING ability.
     */
    public Pickaxe(){
        super("Pickaxe", 'x');
        this.makePortable();
        this.enableAbility(ToolAbility.BREAKING);
    }
}
