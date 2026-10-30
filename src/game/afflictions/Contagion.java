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
 * <p>Undead and actors that already carry Contagion are skipped by
 * {@link CurseApplier}.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Contagion extends Affliction {

    /** How many turns Contagion lasts. */
    private static final int DURATION = 6;

    /** Contagion spreads every turn. */
    private static final int INTERVAL = 1;

    /**
     * Creates a Contagion curse with its full duration.
     */
    public Contagion() {
        super("Contagion", DURATION, INTERVAL);
    }

    /**
     * Spreads Contagion to every adjacent actor and blights the host's tile.
     *
     * @param host     the entity carrying the curse
     * @param location the host's current location
     */
    @Override
    protected void applyCurse(GameEntity host, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                CurseApplier.apply(destination.getActor(), new Contagion());
            }
        }
        if (location.getGroundAs(TemporaryGround.class) == null) {
            location.setGround(new Blight(location.getGround()));
        }
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
     * Creates a fresh Contagion with its full duration.
     *
     * @return a new Contagion
     */
    @Override
    public Affliction copy() {
        return new Contagion();
    }
}