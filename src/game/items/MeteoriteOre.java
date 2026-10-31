package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.interfaces.CelestialRefinable;

/**
 * A portable resource left behind by celestial impacts.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.1.0
 */
public class MeteoriteOre extends Item
        implements CelestialRefinable {

    /**
     * Creates one portable piece of meteorite ore.
     */
    public MeteoriteOre() {
        super("Meteorite Ore", '*');
        makePortable();
    }

    /**
     * Converts meteorite ore into valuable starlit ore.
     *
     * @return the refined ore
     */
    @Override
    public Item refine() {
        return new StarlitOre();
    }
}
