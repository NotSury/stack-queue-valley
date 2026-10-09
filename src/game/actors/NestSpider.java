package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.WebAbility;
import game.interfaces.Hostile;
import game.states.CreatureStateType;

/**
 * A stateful Spider that guards a fixed nest and manages owned webs.
 * Investigates web alerts through Pouncing and walks during Hunting.
 * The factory assembles its states, behaviours and collaborators.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.1.0
 */
public class NestSpider extends StatefulCreature implements Hostile {

    /**
     * Maximum ordinary web-placement distance from the nest.
     */
    public static final int WEB_PLACEMENT_RADIUS = 4;

    /**
     * Maximum Farmer distance from the nest during continued Hunting.
     */
    public static final int PURSUIT_RADIUS = 4;

    /**
     * Distance from the nest that counts as direct intrusion.
     */
    public static final int NEST_THREAT_RADIUS = 1;

    /**
     * Health percentage at or below which recovery is needed.
     */
    public static final int RECOVERY_THRESHOLD = 40;

    /**
     * Health percentage required before optional weaving begins.
     */
    public static final int RECOVERED_THRESHOLD = 80;

    /**
     * Creates an unplaced Spider with a natural weapon and web immunity.
     *
     * @param hitPoints the starting and maximum health
     * @param intrinsicWeapon the Spider's natural weapon
     */
    public NestSpider(
            int hitPoints,
            IntrinsicWeapon intrinsicWeapon
    ) {
        super("Nest Spider", 's', hitPoints, CreatureStateType.NESTING);
        setIntrinsicWeapon(intrinsicWeapon);
        enableAbility(WebAbility.IMMUNE_TO_WEBS);
    }

    /**
     * Provides the existing combat capability without an immediate reaction.
     * State changes are evaluated during the Spider's own turn.
     *
     * @param attacker the attacking actor
     * @param map the map containing this Spider
     * @return an empty string
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map) {
        return "";
    }
}
