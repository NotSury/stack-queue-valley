package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.effects.Explosion;

/**
 * Explodes on impact, dealing 15 damage to every actor on a tile adjacent to the
 * target. The attacker is standing next to the target, so the blast hits them too.
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.1.0
 */
public class MelonCoating implements Coating {
    /**
     * Damage dealt to each actor caught in the blast.
     */
    private static final int BLAST_DAMAGE = 15;
    /**
     * Explodes around the target.
     *
     * <p>Uses {@link Explosion#explodeAround} so the target itself is not hit by
     * the blast, only the actors on the tiles around it.</p>
     *
     * @param attacker the actor who attacked
     * @param target   the actor who was attacked
     * @param map      the map the attack happened on
     * @return a description of the explosion
     */
    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map) {
        int hit = new Explosion(BLAST_DAMAGE).explodeAround(map.locationOf(target));
        return "The melon coating explodes, hitting " + hit + " nearby actor(s) for "
                + BLAST_DAMAGE + " damage";

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CoatingKind getKind(){
        return CoatingKind.MELON;      // CHARGED in ChargedCoating, MELON in MelonCoating
    }

    /**
     * Name shown in menus and in the sword's name, e.g. "Sword (Melon Coating)".
     *
     * @return the display name of this coating
     */
    @Override
    public String toString(){
        return "Melon Coating";
    }
}
