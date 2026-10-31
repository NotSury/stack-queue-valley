package game.effects;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A temporary, non-portable area effect centred on one map location.
 *
 * <p>The field affects nearby locations once per map tick, expires after
 * its configured number of ticks, and removes itself from the ground.
 * Concrete fields implement their own effect and optional expiry behaviour.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public abstract class EnvironmentalField extends Item {

    private static final int MAX_RADIUS = 2;
    private static final int MIN_DURATION = 2;
    private static final int MAX_DURATION = 4;
    private static final int MAX_BONUS_TURNS = 4;

    private final int radius;
    private int remainingTurns;
    private int bonusTurns;
    private Location centre;

    /**
     * Creates a field using radius and duration derived from an impact profile.
     * Radius is capped at two tiles and duration is clamped to two to four ticks.
     *
     * @param name displayed field name
     * @param displayChar character shown while the field is on the ground
     * @param profileRadius positive impact radius
     * @param profileDuration positive impact duration
     */
    protected EnvironmentalField(
            String name,
            char displayChar,
            int profileRadius,
            int profileDuration) {

        super(Objects.requireNonNull(name, "name"), displayChar);

        if (profileRadius <= 0 || profileDuration <= 0) {
            throw new IllegalArgumentException(
                    "Field radius and duration must be positive"
            );
        }

        this.radius = Math.min(profileRadius, MAX_RADIUS);

        this.remainingTurns = Math.max(
                MIN_DURATION,
                Math.min(profileDuration, MAX_DURATION)
        );

        makeNonPortable();
    }

    /**
     * Places this new field at its impact location, exactly once.
     *
     * @param location the main impact location
     */
    public final void placeAt(Location location) {

        Objects.requireNonNull(location, "location");

        if (centre != null) {
            throw new IllegalStateException(
                    "This field has already been placed"
            );
        }

        centre = location;
        location.addItem(this);
    }

    /**
     * Returns whether this field is still active and present on the map.
     *
     * @return true if placed, present, and not expired
     */
    public final boolean isActive() {
        return centre != null
                && remainingTurns > 0
                && centre.getItems().contains(this);
    }

    /**
     * Returns the effective field radius.
     *
     * @return radius in tiles
     */
    public final int getRadius() {
        return radius;
    }

    /**
     * Returns how many effect ticks are remaining.
     *
     * @return remaining effect ticks
     */
    public final int getRemainingTurns() {
        return remainingTurns;
    }

    /**
     * Extends the field duration without exceeding the bonus-turn limit.
     *
     * @param requestedTurns number of additional turns requested
     * @return number of turns actually added, or zero if unavailable
     */
    protected final int extendDuration(int requestedTurns) {
        if (requestedTurns <= 0) {
            throw new IllegalArgumentException(
                    "Extension must be positive"
            );
        }

        if (!isActive()) {
            return 0;
        }

        int added = Math.min(
                requestedTurns,
                MAX_BONUS_TURNS - bonusTurns
        );

        remainingTurns += added;
        bonusTurns += added;

        return added;
    }

    /**
     * Returns the location where this field was placed, if any.
     *
     * @return its centre, or null before placement
     */
    public final Location getCentre() {
        return centre;
    }

    /**
     * Applies one field tick and expires after the last effect.
     *
     * @param currentLocation location containing this field
     */
    @Override
    public final void tick(Location currentLocation) {

        if (!isActive() || currentLocation != centre) {
            return;
        }

        applyFieldEffect(centre);
        remainingTurns--;

        if (remainingTurns == 0) {
            onExpiry(centre);
            centre.removeItem(this);
        }
    }

    /**
     * Performs one turn of the specific environmental effect.
     *
     * @param centre the field's impact location
     */
    protected abstract void applyFieldEffect(Location centre);

    /**
     * Performs any final effects, such as repairing meteor impact craters.
     * The default implementation has no additional expiry behaviour.
     *
     * @param centre the field's impact location
     */
    protected void onExpiry(Location centre) {
        // Optional for subclasses.
    }

    /**
     * Takes a snapshot of locations within the field's circular radius.
     *
     * @param centre field centre
     * @return immutable list of affected locations
     */
    protected final List<Location> affectedLocations(Location centre) {

        Objects.requireNonNull(centre, "centre");

        GameMap map = centre.map();
        List<Location> affected = new ArrayList<>();

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {

                long dx = (long) x - centre.x();
                long dy = (long) y - centre.y();

                if (dx * dx + dy * dy
                        <= (long) radius * radius) {

                    affected.add(map.at(x, y));
                }
            }
        }

        return List.copyOf(affected);
    }
}
