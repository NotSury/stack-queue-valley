package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Contagion;
import game.afflictions.CurseApplier;

/**
 * Ground left behind by Contagion (REQ3). Any actor standing on Blight catches
 * Contagion. Blight wears off after a few turns and the original ground returns.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Blight extends TemporaryGround {

    /** How many turns Blight lasts. */
    private static final int DURATION = 4;

    /**
     * Creates Blight on top of the existing ground.
     *
     * @param previous the ground to restore when the Blight wears off
     */
    public Blight(Ground previous) {
        super(':', "Blight", previous, DURATION);
    }

    /**
     * Infects the actor standing on this tile with Contagion.
     *
     * @param actor    the actor on this tile
     * @param location the location of this ground
     */
    @Override
    protected void affectOccupant(Actor actor, Location location) {
        CurseApplier.apply(actor, new Contagion());
    }
}