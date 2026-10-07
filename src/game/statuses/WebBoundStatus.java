package game.statuses;

import edu.monash.fit2099.engine.capabilities.Status;

/**
 * Records one pending turn lost to a web.
 *
 * Remains active until the actor's turn handler removes it.
 * Map ticks do not expire the pending binding.
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
