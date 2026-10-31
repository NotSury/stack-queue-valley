package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.interfaces.CelestialRefinable;
import game.interfaces.GravitationalFuel;

/**
 * Portable meteorite ore which can be refined by celestial
 * blessings or absorbed to recharge gravity fields.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.2.0
 */
public class MeteoriteOre extends Item
        implements CelestialRefinable, GravitationalFuel {

    /**
     * Creates one portable piece of meteorite ore.
     */
    public MeteoriteOre() {
        super("Meteorite Ore", '*');
        makePortable();
    }

    /**
     * Converts meteorite ore into Starlit Ore.
     *
     * @return the refined item
     */
    @Override
    public Item refine() {
        return new StarliteOre();
    }

    /**
     * Provides one additional tick of gravitational energy.
     *
     * @return one bonus turn
     */
    @Override
    public int getGravityCharge() {
        return 1;
    }
}
