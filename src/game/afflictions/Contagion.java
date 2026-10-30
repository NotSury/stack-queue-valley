package game.afflictions;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Blight;
import game.grounds.TemporaryGround;

/**
 * The Bat's curse (REQ3). Every turn, Contagion jumps to every actor standing
 * next to the host (including the Farmer), and the host's tile turns into
 * Blight, which infects anyone who steps on it later.
 *
 * <p>Contagion weakens as it spreads: each new infection is one generation
 * further from the source, and a Contagion at the maximum generation can no
 * longer spread or blight. This guarantees every outbreak dies out, instead of
 * two actors standing next to each other re-infecting each other forever.</p>
 *
 * <p>Undead and actors that already carry Contagion are skipped by
 * {@link CurseApplier}.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.1.0
 */
public class Contagion extends Affliction {

    /** How many turns Contagion lasts. */
    private static final int DURATION = 6;

    /** Contagion spreads every turn. */
    private static final int INTERVAL = 1;

    /** A Contagion at this generation can no longer spread or blight. */
    private static final int MAX_GENERATION = 2;

    /** How many times this infection has been passed on from the original source. */
    private final int generation;

    /**
     * Creates a new outbreak of Contagion (generation 0) with its full duration.
     */
    public Contagion() {
        this(0);
    }

    /**
     * Creates Contagion at the given generation with its full duration.
     *
     * @param generation how many times the infection has been passed on
     */
    public Contagion(int generation) {
        super("Contagion", DURATION, INTERVAL);
        this.generation = generation;
    }

    /**
     * Spreads a weaker Contagion to every adjacent actor and blights the host's
     * tile (unless it is already cursed ground), as long as this infection has
     * not reached the maximum generation.
     *
     * @param host     the entity carrying the curse
     * @param location the host's current location
     */
    @Override
    protected void applyCurse(GameEntity host, Location location) {
        if (!canSpread()) {
            return;
        }
        int nextGeneration = generation + 1;
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                CurseApplier.apply(destination.getActor(), new Contagion(nextGeneration));
            }
        }
        TemporaryGround.placeOn(location, previous -> new Blight(previous, nextGeneration));
    }

    /**
     * Checks whether this infection is still strong enough to spread.
     *
     * @return true if this Contagion has not reached the maximum generation
     */
    public boolean canSpread() {
        return generation < MAX_GENERATION;
    }

    /**
     * Returns the Contagion curse type.
     *
     * @return {@link CurseType#CONTAGION}
     */
    @Override
    public CurseType getType() {
        return CurseType.CONTAGION;
    }

    /**
     * Creates a fresh Contagion of the same generation with its full duration.
     *
     * @return a new Contagion
     */
    @Override
    public Affliction copy() {
        return new Contagion(generation);
    }
}