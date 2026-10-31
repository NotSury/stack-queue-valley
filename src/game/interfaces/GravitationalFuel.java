package game.interfaces;

/**
 * A resource capable of recharging a gravitational field.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface GravitationalFuel {

    /**
     * Returns how many extra field ticks one item supplies.
     *
     * @return a positive gravity charge
     */
    int getGravityCharge();
}
