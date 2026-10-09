package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Deals 30 extra damage on top of the weapon's own damage.
 *
 * <p>Assumption: the spec says a coating "takes effect on every attack, including
 * one that misses", so the extra damage is applied even when the sword misses.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.1.0
 */
public class ChargedCoating implements Coating {

    /**
     * Extra damage dealt on every attack.
     */
    private static final int EXTRA_DAMAGE = 30;
    /**
     * Shocks the target for extra damage.
     *
     * @param attacker the actor who attacked
     * @param target   the actor who was attacked
     * @param map      the map the attack happened on
     * @return a description of the shock
     */
    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map){
        target.hurt(EXTRA_DAMAGE);
        return "The charge shocks " + target + " for " + EXTRA_DAMAGE + " extra damage.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoatingKind getKind(){
        return CoatingKind.CHARGED;      // CHARGED in ChargedCoating, MELON in MelonCoating
    }


    /**
     * Name shown in menus and in the sword's name, e.g. "Sword (Charged Coating)".
     *
     * @return the display name of this coating
     */
    @Override
    public String toString(){
        return "Charged Coating";
    }
}
