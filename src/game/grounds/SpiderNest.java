package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Represents the Nest Spider's visible, fixed home.
 * Actors can walk across the nest, but it does not provide
 * tilling, watering or ordinary web-placement capabilities.
 *
 * @author Surya Muni Peragas
 * @version 1.0.0
 */
public class SpiderNest extends Ground {

    /**
     * Creates a visible Spider nest.
     */
    public SpiderNest() {
        super('&', "Spider Nest");
    }
}
