package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.NecromancyCapability;
import game.interfaces.Cursed;

/**
 * The remains of a dead cursed creature (REQ3). Remains stay on the map and
 * remember which creature died there and what ground was underneath, so the
 * creature can later be raised as an undead minion and the original ground
 * restored.
 *
 * <p>Remains count as cursed ground, so no other curse can cover them.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public class Remains extends Ground {

    private final Ground previous;
    private final Cursed deceased;

    /**
     * Creates remains on top of the existing ground.
     *
     * @param previous the ground underneath, restored after the remains are raised
     * @param deceased the cursed creature that died here
     */
    public Remains(Ground previous, Cursed deceased) {
        super('%', "Remains");
        this.previous = previous;
        this.deceased = deceased;
        this.enableAbility(NecromancyCapability.CURSED_GROUND);
    }
}