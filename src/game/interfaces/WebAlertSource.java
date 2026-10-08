package game.interfaces;

import game.traps.WebAlert;

/**
 * Gives read and clear access to the most recent web-trigger alert.
 *
 * <p>States that react to triggered webs depend only on this interface,
 * not on web placement (Interface Segregation Principle).</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */

public interface WebAlertSource {

    /**
     * Returns the most recent alert that has not been cleared.
     * The alert remains after the triggering web is consumed or expires.
     *
     * @return the latest alert, or null if there is none
     */
    WebAlert getLatestAlert();

    /**
     * Clears the latest alert, but only if it is still the given event,
     * so a newer alert raised in the meantime is kept.
     *
     * @param eventId the identifier of the alert that was investigated
     * @return true if the alert was cleared, false if it was not the latest
     */
    boolean clearAlert(long eventId);

}
