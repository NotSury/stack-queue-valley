package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.effects.Explosion;
import game.weapons.CreatureWeapon;

public class CaveGhost extends HostileCreature{

    private static final int DEATH_BLAST_DAMAGE = 50;

    public CaveGhost(){
        super("Cave Ghost",'G',250, new CreatureWeapon(25,"strikes",
                        75, "spectral touch"));
    }

    @Override
    public String unconscious(Actor otherActor, GameMap map){
        int hit = new Explosion(DEATH_BLAST_DAMAGE).explodeAround(map.locationOf(this));
        return this + " explodes as it dies, hitting " + hit + " nearby actor(s) for "
                + DEATH_BLAST_DAMAGE + " damage!" + System.lineSeparator() + super.unconscious(otherActor, map);
    }
}
