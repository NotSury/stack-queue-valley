package game.capabilities;
/**
 * ToolAbility represents the abilities that can be provided by tools.
 *
 * <p>Actions can check for these abilities instead of depending on
 * specific concrete tool classes. This allows different tools to provide
 * the same capability without requiring changes to the corresponding
 * action classes.</p>
 *
 * @author Lee Le Si 34599223
 * @version 1.0.0
 */
public enum ToolAbility {
    /**
     * Ability that allows an actor to perform tilling actions.
     */
    TILLING,
    /**
     * Ability that allows an actor to perform watering actions.
     */
    WATERING
}
