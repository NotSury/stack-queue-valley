package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.coatings.Coating;
import game.coatings.FireCoating;
import game.interfaces.CoatingSource;
import game.interfaces.MovementHazard;
import game.statuses.HealthOverTimeStatus;

/**
 * A non-portable fire that remains on the ground for a limited number of turns and
 * applies a damage-over-time effect to actors standing on it.
 *
 *  <p>Fire is also a coating source: an actor standing next to it can use it to
 *  coat a weapon with a fire coating.</p>
 *
 *
 * @author Khoo Lynn 34598901
 * @author Lee Le Si 34599223
 * @version 1.2.0
 */
public class Fire extends Item implements MovementHazard, CoatingSource {
    private int remainingTurns;
    private final int damagePerTurn;
    private final int burnDuration;

    public Fire(int remainingTurns, int damagePerTurn, int burnDuration){
        super("Fire", 'f');
        this.remainingTurns = remainingTurns;
        this.damagePerTurn = damagePerTurn;
        this.burnDuration = burnDuration;
    }

    @Override
    public void tick(Location currentLocation){
        if (currentLocation.containsAnActor()){
            currentLocation.getActor().addStatus(new HealthOverTimeStatus(-damagePerTurn, burnDuration));
        }

        remainingTurns--;

        if (remainingTurns <= 0){
            currentLocation.removeItem(this);
        }
    }

    /**
     * Fire on an adjacent tile can coat a weapon with fire.
     *
     * @return a new fire coating
     */
    @Override
    public Coating getCoating(){
        return new FireCoating();
    }
}
