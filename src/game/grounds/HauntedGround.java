package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.CurseApplier;
import game.afflictions.SoulTether;

/**
 * Haunted tiles left by a dead Cave Ghost, the Wraith and Soul Tether (REQ3).
 * Any actor standing on Haunted Ground gets a Soul Tether. The haunting fades
 * after a few turns and the original ground returns.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class HauntedGround extends TemporaryGround {

    /** How many turns the haunting lasts. */
    private static final int DURATION = 5;

    /**
     * Creates Haunted Ground on top of the existing ground.
     *
     * @param previous the ground to restore when the haunting fades
     */
    public HauntedGround(Ground previous) {
        super('?', "Haunted Ground", previous, DURATION);
    }

    /**
     * Gives a Soul Tether to the actor standing on this tile.
     *
     * @param actor    the actor on this tile
     * @param location the location of this ground
     */
    @Override
    protected void affectOccupant(Actor actor, Location location) {
        CurseApplier.apply(actor, new SoulTether());
    }
}