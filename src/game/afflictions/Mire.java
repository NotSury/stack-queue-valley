package game.afflictions;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.MireGround;
import game.grounds.TemporaryGround;

/**
 * The Slime's curse (REQ3). Every 2 turns, the empty tiles around the host turn
 * into swamp (MireGround) that nobody can walk onto and that swallows any items
 * lying on it. The host's own tile stays solid, and tiles with an actor on them
 * are skipped so nobody is trapped inside the swamp.
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Mire extends Affliction {

    /** How many turns Mire lasts. */
    private static final int DURATION = 6;

    /** The swamp forms every 2 turns. */
    private static final int INTERVAL = 2;

    /**
     * Creates a Mire curse with its full duration.
     */
    public Mire() {
        super("Mire", DURATION, INTERVAL);
    }

    /**
     * Turns every empty, non-cursed tile around the host into swamp.
     *
     * @param host     the entity carrying the curse
     * @param location the host's current location
     */
    @Override
    protected void applyCurse(GameEntity host, Location location) {
        TemporaryGround.placeAround(location, MireGround::new, true);
    }

    /**
     * Returns the Mire curse type.
     *
     * @return {@link CurseType#MIRE}
     */
    @Override
    public CurseType getType() {
        return CurseType.MIRE;
    }

    /**
     * Creates a fresh Mire with its full duration.
     *
     * @return a new Mire
     */
    @Override
    public Affliction copy() {
        return new Mire();
    }
}