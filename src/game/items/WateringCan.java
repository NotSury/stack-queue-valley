package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.capabilities.Ability;

/**
 * Represents a portable watering can that can be used by the Farmer for watering.
 * The watering can has an unlimited water supply.
 */
public class WateringCan extends Item {

    /**
     * Constructs a portable watering that provides the ability to water ground.
     */
    public WateringCan() {
        super("Watering Can", 'w');
        this.makePortable();
        this.enableAbility(Ability.WATER);
    }
}
