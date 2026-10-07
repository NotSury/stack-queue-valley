package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.WebAbility;
import game.interfaces.Hostile;
import game.states.CreatureStateType;

/**
 * A hostile stateful Spider that guards a fixed nest, weaves traps
 * and uses tactical jumps when pursuing the Farmer.
 * The factory assembles its states and web network.
 * The Spider is immune to web bindings.
 *
 * @author Surya Muni Peragas
 * @version 1.0.0
 */
public class NestSpider extends StatefulCreature implements Hostile {

    /**
     * Maximum distance used to detect an active Farmer.
     */
    public static final int DETECTION_RADIUS = 4;

    /**
     * Maximum web-placement distance from the fixed nest.
     */
    public static final int WEB_PLACEMENT_RADIUS = 3;

    /**
     * Maximum distance from the nest used for pursuit assessment.
     */
    public static final int PURSUIT_RADIUS = 6;

    /**
     * Creates an unplaced Spider with a natural weapon and web immunity.
     * Its states must be registered before the initial state is activated.
     *
     * @param hitPoints the Spider's starting and maximum health
     * @param intrinsicWeapon the weapon used for its natural attacks
     */
    public NestSpider(int hitPoints, IntrinsicWeapon intrinsicWeapon) {
        super("Nest Spider", 's', hitPoints, CreatureStateType.NESTING);

        setIntrinsicWeapon(intrinsicWeapon);
        enableAbility(WebAbility.IMMUNE_TO_WEBS);
    }

    /**
     * Provides the reaction required by the shared combat interface.
     * State transitions are evaluated during the Spider's own turn.
     *
     * @param attacker the actor that attacked this Spider
     * @param map the map containing the Spider
     * @return an empty string because no immediate reaction is performed
     */
    @Override
    public String respondToAttack(Actor attacker, GameMap map) {
        return "";
    }
}
