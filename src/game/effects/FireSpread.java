package game.effects;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Fire;

/**
 * Handles the creation of fire around a location.
 *
 * @author Khoo Lynn 34598901
 * @version 1.0.0
 */
public class FireSpread {
    private final int fireDuration;
    private final int damagePerTurn;
    private final int burnDuration;

    /**
     * Creates a FireSpread configuration.
     *
     * @param fireDuration number of turns the fire remains.
     * @param damagePerTurn damage caused per burn turn.
     * @param burnDuration number of turns the burn effect lasts.
     */
    public FireSpread(int fireDuration, int damagePerTurn, int burnDuration){
        this.fireDuration = fireDuration;
        this.damagePerTurn = damagePerTurn;
        this.burnDuration = burnDuration;
    }

    /**
     * Creates fire on every location adjacent to the give location.
     *
     * @param location the centre location.
     */
    public void applyAround(Location location){
        for (Exit exit : location.getExits()){
            Location adjacentLocation = exit.getDestination();

            adjacentLocation.addItem(new Fire(fireDuration, damagePerTurn, burnDuration));
        }
    }
}
