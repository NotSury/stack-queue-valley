package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Contagion;
import game.afflictions.CurseApplier;

/**
 * Ground left behind by Contagion and by a dead Bat (REQ3). Any actor standing
 * on Blight catches Contagion of the generation stored in the Blight. Blight
 * wears off after a few turns and the original ground returns.
 *
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public class Blight extends TemporaryGround {

    /** How many turns Blight lasts. */
    private static final int DURATION = 4;

    /** The Contagion generation this Blight passes on. */
    private final int generation;

    /**
     * Creates Blight from a new outbreak (generation 0), e.g. a dead Bat's bones.
     *
     * @param previous the ground to restore when the Blight wears off
     */
    public Blight(Ground previous) {
        this(previous, 0);
    }

    /**
     * Creates Blight that passes on Contagion of the given generation.
     *
     * @param previous   the ground to restore when the Blight wears off
     * @param generation the Contagion generation this Blight passes on
     */
    public Blight(Ground previous, int generation) {
        super(';', "Blight", previous, DURATION);
        this.generation = generation;
    }

    /**
     * Infects the actor standing on this tile with Contagion.
     *
     * @param actor    the actor on this tile
     * @param location the location of this ground
     */
    @Override
    protected void affectOccupant(Actor actor, Location location) {
        CurseApplier.apply(actor, new Contagion(generation));
    }
}