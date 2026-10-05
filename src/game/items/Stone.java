package game.items;

import edu.monash.fit2099.engine.items.Item;

// TEMPORARY STONE ITEM for REQ 3 integration

public class Stone extends Item
{
    public Stone()
    {
        super("Stone", 's');
        this.makePortable();;
    }
}
