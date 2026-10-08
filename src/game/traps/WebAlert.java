package game.traps;

import edu.monash.fit2099.engine.positions.Location;

public final class WebAlert {
    private final long eventId;
    private final Location location;

    public WebAlert(long eventId, Location location) {
        this.eventId = eventId;
        this.location = location;
    }

    public long getEventId() {
        return eventId;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "web alert #"  + eventId + " at (" + location.x() + ", " + location.y() + ")";
    }
}
