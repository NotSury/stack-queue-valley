package game.interfaces;

/**
 * Provides a capability for locations protected by
 * Nature's Sanctuary.
 *
 * @author <Vanessa Liau Jia Yee> <34599398>
 * @version 1.0.0
 */
public interface SanctuaryProtected {

    /**
     * Checks if a hostile creature may enter the protected area.
     *
     * @return true if hostile entry is allowed.
     */
    boolean allowsHostileEntry();
}
