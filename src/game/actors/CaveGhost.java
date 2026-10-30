package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.afflictions.Affliction;
import game.afflictions.SoulTether;
import game.effects.Explosion;
import game.grounds.HauntedGround;
import game.grounds.TemporaryGround;
import game.interfaces.Cursed;
import game.weapons.CreatureWeapon;
/**
 * A cave ghost: 250 HP, strikes for 25 damage with a 75% chance to hit.
 * When a blow kills it, it explodes, dealing 50 damage to every actor next to it.
 *
 * <p>The Cave Ghost carries Soul Tether (REQ3). Its killer inherits Soul Tether,
 * and the tiles around its remains become Haunted Ground when it dies.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.1.0
 */
public class CaveGhost extends HostileCreature implements Cursed {
    /**
     * Starting health of a cave ghost.
     */
    private static final int MAX_HP = 250;
    /**
     * Damage of one strike.
     */
    private static final int STRIKE_DAMAGE = 25;
    /**
     * Chance for a strike to hit, as a percentage.
     */
    private static final int HIT_RATE = 75;
    /**
     * Damage dealt to each adjacent actor when the ghost explodes.
     */
    private static final int DEATH_BLAST_DAMAGE = 50;

    /**
     * Constructor for CaveGhost.
     */
    public CaveGhost(){
        super("Cave Ghost",'G',MAX_HP, new CreatureWeapon(STRIKE_DAMAGE,"strikes",
                HIT_RATE, "spectral touch"));
    }

    /**
     * Called when another actor's blow kills the ghost: it explodes, then is removed.
     *
     * <p>The blast uses {@link Explosion#explodeAround}, so it hits the actors on
     * adjacent tiles but not the ghost's own tile. Dying from burning has no
     * attacker and uses unconscious(GameMap) instead, so there is no explosion.</p>
     *
     * @param otherActor the actor whose blow killed the ghost
     * @param map        the map the ghost is on
     * @return a description of the explosion and the ghost's death
     */
    @Override
    public String unconscious(Actor otherActor, GameMap map){
        int hit = new Explosion(DEATH_BLAST_DAMAGE).explodeAround(map.locationOf(this));
        return this + " explodes as it dies, hitting " + hit + " nearby actor(s) for "
                + DEATH_BLAST_DAMAGE + " damage!" + System.lineSeparator() + super.unconscious(otherActor, map);
    }

    /**
     * Creates a fresh Soul Tether to pass to the ghost's killer.
     *
     * @return a new Soul Tether
     */
    @Override
    public Affliction curse() {
        return new SoulTether();
    }

    /**
     * Haunts every tile next to the ghost's remains.
     *
     * @param remains the location where the ghost died
     * @return a description of the haunting
     */
    @Override
    public String onDeath(Location remains) {
        int haunted = TemporaryGround.placeAround(remains, HauntedGround::new, false);
        return this + "'s spirit haunts " + haunted + " tile(s) around its remains.";
    }
}