package game.interfaces;

import edu.monash.fit2099.engine.items.Item;

/**
 * A resource that can be transformed by a celestial blessing.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface CelestialRefinable {

    /**
     * Creates the refined version of this resource.
     *
     * @return a new refined item
     */
    Item refine();
}
