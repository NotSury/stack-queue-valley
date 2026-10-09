package game.statuses;

import edu.monash.fit2099.engine.capabilities.Status;

/**
 * Records one pending turn lost to a web.
 * Remains active until the actor's turn handler removes it.
 * Map ticks do not expire the pending binding.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public class WebBoundStatus implements Status {

    /**
     * Keeps the binding pending until it is explicitly consumed.
     *
     * @return true while this status remains attached to the actor
     */
    @Override
    public boolean isStatusActive() {
        return true;
    }
}
