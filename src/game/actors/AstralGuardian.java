package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.MeteoriteOre;
import game.weapons.CreatureWeapon;

/**
 * A celestial creature released by dangerous meteor impacts.
 *
 * <p>The guardian uses the existing hostile attack and wander
 * behaviours. Defeating it leaves one meteorite ore.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class AstralGuardian extends HostileCreature {

    private static final int HEALTH = 60;
    private static final int ATTACK_DAMAGE = 16;
    private static final int HIT_RATE = 70;

    /**
     * Creates an Astral Guardian with its natural attack.
     */
    public AstralGuardian() {
        super(
                "Astral Guardian",
                'A',
                HEALTH,
                new CreatureWeapon(
                        ATTACK_DAMAGE,
                        "strikes",
                        HIT_RATE,
                        "astral claws"
                )
        );
    }

    /**
     * Drops ore when the guardian is defeated by another actor.
     *
     * @param attacker actor responsible for defeating the guardian
     * @param map map containing the guardian
     * @return defeat message
     */
    @Override
    public String unconscious(Actor attacker, GameMap map) {
        dropOre(map);
        return super.unconscious(attacker, map);
    }

    /**
     * Drops ore when the guardian falls to an environmental hazard.
     *
     * @param map map containing the guardian
     * @return defeat message
     */
    @Override
    public String unconscious(GameMap map) {
        dropOre(map);
        return super.unconscious(map);
    }

    /**
     * Leaves one meteorite ore at the guardian's location.
     *
     * @param map map containing the guardian
     */
    private void dropOre(GameMap map) {
        if (map.contains(this)) {
            Location location = map.locationOf(this);
            location.addItem(new MeteoriteOre());
        }
    }
}
