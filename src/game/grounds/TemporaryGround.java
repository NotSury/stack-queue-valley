package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Cursed ground that lasts a fixed number of turns and then turns back into the
 * ground that was there before (REQ3). Blight, Mire and Haunted Ground all share
 * this restore logic, so it is written once (DRY).
 *
 * <p>Each turn, if an actor is standing on the tile, {@link #affectOccupant} is
 * called so each subclass can apply its own effect.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public abstract class TemporaryGround extends Ground {

    private final Ground previous;
    private int turnsLeft;

    /**
     * Creates temporary ground.
     *
     * @param displayChar the character shown on the map
     * @param name        the ground name
     * @param previous    the ground to restore when this one expires
     * @param duration    how many turns this ground lasts
     */
    protected TemporaryGround(char displayChar, String name, Ground previous, int duration) {
        super(displayChar, name);
        this.previous = previous;
        this.turnsLeft = duration;
    }

    /**
     * Applies this ground's effect to anyone standing on it, then counts down and
     * restores the previous ground when the time runs out.
     *
     * @param location the location of this ground
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            affectOccupant(location.getActor(), location);
        }
        turnsLeft--;
        if (turnsLeft <= 0) {
            location.setGround(previous);
        }
    }

    /**
     * Applies this ground's effect to an actor standing on it. Does nothing by default.
     *
     * @param actor    the actor on this tile
     * @param location the location of this ground
     */
    protected void affectOccupant(Actor actor, Location location) {
        // most temporary grounds have no effect on occupants unless they override this
    }
}