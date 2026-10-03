package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
/**
 * The natural attack of a hostile creature (bite, slam, ...).
 *
 * <p>Needed because the engine's IntrinsicWeapon is abstract. Hit chance and
 * damage are handled by the engine's IntrinsicWeapon.attack().</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public class CreatureWeapon extends IntrinsicWeapon {
    /**
     * Constructor for CreatureWeapon.
     *
     * @param damage  damage per hit
     * @param verb    word used in messages, e.g. "bites"
     * @param hitRate chance to hit, as a percentage
     * @param name    name of the weapon, e.g. "fangs"
     */
    public CreatureWeapon( int damage, String verb, int hitRate, String name){
        super(damage, verb, hitRate, name);
    }
}
