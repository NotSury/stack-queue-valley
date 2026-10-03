package game.coatings;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.effects.Explosion;

public class MelonCoating implements Coating {
    private static final int BLAST_DAMAGE = 15;

    @Override
    public String applyEffect(Actor attacker, Actor target, GameMap map) {
        int hit = new Explosion(BLAST_DAMAGE).explodeAround(map.locationOf(target));
        return "The melon coating explodes, hitting " + hit + " nearby actor(s) for "
                + BLAST_DAMAGE + " damage";

    }
    @Override
    public String toString(){
        return "Melon Coating";
    }
}
