package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import game.interfaces.FreshProduce;
import game.statuses.HealthOverTimeStatus;

/**
 * Represents a Strawberry that can either be fresh or rotten.
 *
 * <p>A fresh Strawberry restores 1 health point per turn for the next
 * 2 turns when consumed. A rotten Strawberry causes the actor to lose
 * 1 health point per turn for the next 3 turns.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class Strawberry extends ConsumableItem implements FreshProduce {

    /**
     * Whether this Strawberry is rotten.
     */
    private final boolean rotten;

    /**
     * Constructor for Strawberry.
     *
     * @param rotten true if the Strawberry is rotten, false if it is fresh
     */
    public Strawberry(boolean rotten) {
        super(rotten ? "Rotten Strawberry" : "Strawberry", 's');
        this.rotten = rotten;
    }

    /**
     * Returns whether this Strawberry is rotten.
     *
     * @return true if rotten, false if fresh
     */
    public boolean isRotten() {
        return rotten;
    }

    /**
     * Checks if this Strawberry is fresh.
     *
     * @return true if the Strawberry is not rotten
     */
    @Override
    public boolean isFresh() {
        return !rotten;
    }

    /**
     * Applies the consumption effect of this Strawberry.
     *
     * <p>A rotten Strawberry applies 1 damage per turn for 3 turns,
     * while a fresh Strawberry restores 1 health per turn for 2 turns.</p>
     *
     * @param actor the actor consuming the Strawberry
     * @return a description of the consumption effect
     */
    @Override
    protected String applyConsumptionEffect(Actor actor) {
        if (rotten) {
            actor.addStatus(new HealthOverTimeStatus(-1, 3));
            return actor
                    + " consumes a rotten Strawberry and will lose 1 health "
                    + "per turn for 3 turns.";
        }

        actor.addStatus(new HealthOverTimeStatus(1, 2));
        return actor
                + " consumes a Strawberry and will restore 1 health "
                + "per turn for 2 turns.";
    }
}