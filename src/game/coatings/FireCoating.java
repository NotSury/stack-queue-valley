package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.statuses.HealthOverTimeStatus;

/**
 * Sets the target burning: 5 damage per turn for 3 turns.
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.1.0
 */
public class FireCoating implements Coating{
    /**
     * Damage dealt to the target every turn while it burns.
     */
    private static final int BURN_DAMAGE_PER_TURN = 5;
    /**
     * Number of turns the target keeps burning.
     */
    private static final int BURN_DURATION = 3;
    /**
     * Sets the target burning by giving it a damage-over-time status.
     *
     * <p>The damage is negative because HealthOverTimeStatus heals for positive
     * values and hurts for negative ones.</p>
     *
     * @param attacker the actor who attacked
     * @param target   the actor who was attacked
     * @param map      the map the attack happened on
     * @return a description of the burn
     */
    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map){
        target.addStatus(new HealthOverTimeStatus(-BURN_DAMAGE_PER_TURN, BURN_DURATION));
        return target + " is set burning for " + BURN_DURATION + " turns. ";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoatingKind getKind(){
        return CoatingKind.FIRE;      // CHARGED in ChargedCoating, MELON in MelonCoating
    }

    /**
     * Name shown in menus and in the sword's name, e.g. "Sword (Fire Coating)".
     *
     * @return the display name of this coating
     */
    @Override
    public String toString(){
        return "Fire Coating";
    }
}
