package game.interfaces;

import game.actors.Spiderling;

/**
 * Provides Spiderling creation for the Nesting state.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public interface SpiderlingFactory {

    /**
     * Creates a Spiderling with its ordinary behaviour configured.
     * The caller places it on the map.
     *
     * @return a new, unplaced Spiderling
     */
    Spiderling createSpiderling();
}
