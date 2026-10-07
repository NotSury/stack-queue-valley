package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.HealthOverTimeStatus;

/**
 * A non-portable fire that remains on the ground for a limited number of turns and
 * applies a damage-over-time effect to actors standing on it.
 *
 * @author Khoo Lynn 34598901
 * @version 1.1.0
 */
public class Fire extends Item {
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
}
