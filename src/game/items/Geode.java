package game.items;

// TODO: Add explosion to the UML REQ 2
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BreakingAction;
import game.effects.Explosion;
import game.effects.FireSpread;
import game.interfaces.Breakable;

import java.util.Random;

/**
 * <p>Breaking a Geode has 50% chance of producing a Diamond. Otherwise, the Geode
 * explodes, damaging actors around the breaking location and creating fire on
 * adjacent locations.</p>
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class Geode extends Item implements Breakable {
    private static final double DIAMOND_CHANCE = 0.5;
    private static final int EXPLOSION_DAMAGE = 25;
    private static final int FIRE_DURATION = 5;
    private static final int BURN_DAMAGE = 10;
    private static final int BURN_DURATION = 4;
    private final Explosion explosion;
    private final Random random;
    private final FireSpread fireSpread;

    /**
     * Creates a portable Geode.
     */
    public Geode(){
        super("Geode", 'G');
        this.makePortable();
        this.random = new Random();
        this.explosion = new Explosion(EXPLOSION_DAMAGE);
        this.fireSpread = new FireSpread(FIRE_DURATION, BURN_DAMAGE, BURN_DURATION);
    }

    private void removeGeode(Actor actor, Location location, boolean carried){
        if (carried){
            actor.getInventory().remove(this);
        } else{
            location.removeItem(this);
        }
    }

    private void produceDiamond(Actor actor, Location location, boolean carried){
        Diamond diamond = new Diamond();
        if (carried){
            actor.getInventory().add(diamond);
        } else{
            location.addItem(diamond);
        }
    }

    private void explode(Location location){
        explosion.explode(location);
        fireSpread.applyAround(location);
    }

    /**
     * Provides a breaking action while the Geode is carried by an actor.
     *
     * @param owner the actor that owns the item
     * @param map the map where the actor is performing the action on
     * @return the action available for this Geode.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map){
        ActionList actions = new ActionList();
        actions.add(new BreakingAction(this, map.locationOf(owner)));
        return actions;
    }

    /**
     * Provides a breaking action while the Geode is lying on the ground.
     *
     * @param location the location of the ground on which the item lies
     * @return the actions available for this Geode.
     */
    @Override
    public ActionList allowableActions(Location location){
        ActionList actions = new ActionList();
        actions.add(new BreakingAction(this, location));
        return actions;
    }

    /**
     * Breaks the Geode and produces either a Diamond or an explosion.
     *
     * @param actor actor breaking the object.
     * @param location location of the object being broken.
     * @return a description of the breaking result.
     */
    @Override
    public String breakBy(Actor actor, Location location){
        boolean carried = actor.getInventory().getItems().contains(this);
        removeGeode(actor, location, carried);
        if (random.nextDouble() < DIAMOND_CHANCE){
            produceDiamond(actor, location, carried);
            return actor + " breaks the Geode and finds a Diamond.";
        }
        explode(location);
        return actor + " breaks the Geode and it explodes.";
    }
}
