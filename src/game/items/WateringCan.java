package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.capabilities.ToolAbility;
/**
 * WateringCan represents a portable tool that allows an actor to
 * perform watering actions.
 *
 * <p>The WateringCan provides the WATERING ability and has an
 * unlimited water supply, so it does not need to be refilled.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class WateringCan extends Item {
    /**
     * Constructor for the WateringCan class.
     *
     * <p>Creates a portable WateringCan and enables the WATERING ability.</p>
     */
    public WateringCan() {
        super("Watering Can",'w');
        this.makePortable();
        this.enableAbility(ToolAbility.WATERING);
    }
}
