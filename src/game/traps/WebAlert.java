package game.traps;

import edu.monash.fit2099.engine.positions.Location;

/**
 * One web-trigger event: where an owned web caught an actor, and a
 * number identifying the event.
 *
 * <p>Event ids increase with every trigger, so a newer alert always has
 * a larger id. The alert stays valid after its web has been consumed.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public final class WebAlert {
    /**
     * Identifier of this trigger event; larger means more recent.
     */
    private final long eventId;
    /**
     * Location of the web that was triggered.
     */
    private final Location location;

    /**
     * Creates an alert for one trigger event.
     *
     * @param eventId the identifier of the event
     * @param location the location of the triggered web
     */
    public WebAlert(long eventId, Location location) {
        this.eventId = eventId;
        this.location = location;
    }

    /**
     * Returns the identifier of this event.
     *
     * @return the event identifier
     */
    public long getEventId() {
        return eventId;
    }

    /**
     * Returns where the web was triggered.
     *
     * @return the triggered location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Describes the alert, e.g. "web alert #3 at (4, 2)".
     *
     * @return a short description of this alert
     */
    @Override
    public String toString() {
        return "web alert #"  + eventId + " at (" + location.x() + ", " + location.y() + ")";
    }
}
