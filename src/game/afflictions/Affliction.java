package game.afflictions;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A curse carried by an actor (REQ3). Every curse lasts a fixed number of turns
 * and triggers its effect every few turns while it is active.
 *
 * <p>This class is a Template Method: {@link #tickStatus} handles the countdown
 * and timing once, and each subclass only defines what its effect does in
 * {@link #applyCurse}. Effects must change the world around the host (other
 * actors, ground or items) and must not add or remove statuses on the host
 * itself, because the engine is iterating over the host's statuses.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public abstract class Affliction implements Status {

    private final String name;
    private final int interval;
    private int turnsLeft;
    private int turnsElapsed;

    /**
     * Creates a curse.
     *
     * @param name     the curse name shown in messages
     * @param duration how many turns the curse lasts
     * @param interval the effect triggers every this many turns
     */
    protected Affliction(String name, int duration, int interval) {
        this.name = name;
        this.turnsLeft = duration;
        this.interval = interval;
        this.turnsElapsed = 0;
    }

    /**
     * Counts down the curse and triggers its effect every {@code interval} turns.
     *
     * @param host     the entity carrying the curse
     * @param location the host's current location
     */
    @Override
    public final void tickStatus(GameEntity host, Location location) {
        if (!isStatusActive()) {
            return;
        }
        turnsElapsed++;
        if (turnsElapsed % interval == 0) {
            applyCurse(host, location);
        }
        turnsLeft--;
    }

    /**
     * Returns whether the curse still has turns left. The engine removes it automatically once this is false.
     *
     * @return true while the curse is active
     */
    @Override
    public boolean isStatusActive() {
        return turnsLeft > 0;
    }

    /**
     * Performs this curse's effect on the world around the host.
     *
     * @param host     the entity carrying the curse
     * @param location the host's current location
     */
    protected abstract void applyCurse(GameEntity host, Location location);

    /**
     * Returns which kind of curse this is.
     *
     * @return the curse type
     */
    public abstract CurseType getType();

    /**
     * Creates a fresh copy of this curse with its full duration, so the same
     * curse object is never shared between two actors.
     *
     * @return a new curse of the same kind
     */
    public abstract Affliction copy();

    /**
     * Returns the curse name.
     *
     * @return the curse name
     */
    @Override
    public String toString() {
        return name;
    }
}