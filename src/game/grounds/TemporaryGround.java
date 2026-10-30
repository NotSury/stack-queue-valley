package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.NecromancyCapability;

import java.util.function.Function;

/**
 * Cursed ground that lasts a fixed number of turns and then turns back into the
 * ground that was there before (REQ3). Blight, Mire and Haunted Ground all share
 * this restore logic, so it is written once (DRY).
 *
 * <p>Each turn, if an actor is standing on the tile, {@link #affectOccupant} is
 * called so each subclass can apply its own effect. The static helpers
 * {@link #placeOn} and {@link #placeAround} are the single place where cursed
 * ground is created, so the rule "never cover cursed ground with more cursed
 * ground" is written once.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.1.0
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
        this.enableAbility(NecromancyCapability.CURSED_GROUND);
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

    /**
     * Places new cursed ground on a location, unless the location already has
     * cursed ground or cursed remains.
     *
     * @param location the location to curse
     * @param factory  creates the new ground from the ground currently there
     * @return true if the cursed ground was placed
     */
    public static boolean placeOn(Location location, Function<Ground, TemporaryGround> factory) {
        if (location.getGround().hasAbility(NecromancyCapability.CURSED_GROUND)) {
            return false;
        }
        location.setGround(factory.apply(location.getGround()));
        return true;
    }

    /**
     * Places new cursed ground on every tile next to a centre location.
     *
     * @param centre        the location whose neighbours are cursed
     * @param factory       creates the new ground from the ground currently there
     * @param skipOccupied  true to leave tiles with an actor on them untouched
     * @return how many tiles were cursed
     */
    public static int placeAround(Location centre, Function<Ground, TemporaryGround> factory,
                                  boolean skipOccupied) {
        int placed = 0;
        for (Exit exit : centre.getExits()) {
            Location destination = exit.getDestination();
            if (skipOccupied && destination.containsAnActor()) {
                continue;
            }
            if (placeOn(destination, factory)) {
                placed++;
            }
        }
        return placed;
    }
}