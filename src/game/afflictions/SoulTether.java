package game.afflictions;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.HauntedGround;
import game.grounds.TemporaryGround;

import java.util.List;

/**
 * The Cave Ghost's curse (REQ3). Every 3 turns, the host is linked to the
 * closest actor within 3 tiles, and every other curse the host carries is
 * copied onto that actor. If the host has no other curse, the linked actor's
 * tile becomes Haunted Ground instead.
 *
 * <p>Soul Tether never copies itself, so it cannot spread endlessly, and every
 * copied curse is a fresh object applied through {@link CurseApplier}.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class SoulTether extends Affliction {

    /** How many turns Soul Tether lasts (three links). */
    private static final int DURATION = 9;

    /** A new link forms every 3 turns. */
    private static final int INTERVAL = 3;

    /** How far away the linked actor can be. */
    private static final int RADIUS = 3;

    /**
     * Creates a Soul Tether curse with its full duration.
     */
    public SoulTether() {
        super("Soul Tether", DURATION, INTERVAL);
    }

    /**
     * Links the host to the closest actor and shares the host's other curses,
     * or haunts that actor's tile if the host has no other curse.
     *
     * @param host     the entity carrying the curse
     * @param location the host's current location
     */
    @Override
    protected void applyCurse(GameEntity host, Location location) {
        Location partnerLocation = findClosestActor(location);
        if (partnerLocation == null) {
            return;
        }

        List<Affliction> otherCurses = host.statusesOf(Affliction.class).stream()
                .filter(curse -> curse.getType() != CurseType.SOUL_TETHER)
                .toList();

        if (otherCurses.isEmpty()) {
            TemporaryGround.placeOn(partnerLocation, HauntedGround::new);
            return;
        }

        for (Affliction curse : otherCurses) {
            CurseApplier.apply(partnerLocation.getActor(), curse.copy());
        }
    }

    /**
     * Finds the location of the closest other actor within the tether radius.
     * Ties are broken by the fixed order in which the engine lists nearby
     * locations, so the result is always predictable.
     *
     * @param centre the host's location
     * @return the location of the closest actor, or null if there is none
     */
    private Location findClosestActor(Location centre) {
        Location closest = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Location candidate : centre.getNearbyLocations(RADIUS)) {
            if (candidate.containsAnActor()) {
                int distance = Math.max(Math.abs(candidate.x() - centre.x()),
                        Math.abs(candidate.y() - centre.y()));
                if (distance < closestDistance) {
                    closest = candidate;
                    closestDistance = distance;
                }
            }
        }
        return closest;
    }

    /**
     * Returns the Soul Tether curse type.
     *
     * @return {@link CurseType#SOUL_TETHER}
     */
    @Override
    public CurseType getType() {
        return CurseType.SOUL_TETHER;
    }

    /**
     * Creates a fresh Soul Tether with its full duration.
     *
     * @return a new Soul Tether
     */
    @Override
    public Affliction copy() {
        return new SoulTether();
    }
}