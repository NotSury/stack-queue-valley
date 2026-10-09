package game.coatings;

/**
 * The kinds of coating a {@link Coating} can be.
 *
 * <p>Used to tell whether two coatings are the same kind (e.g. two fires next to
 * the Farmer) without comparing display text, so the check is enforced by the
 * compiler instead of depending on matching strings.</p>
 *
 * @author <Lee Le Si> <34599223>
 * @version 1.0.0
 */
public enum CoatingKind {
    /** Burns the target over time. */
    FIRE,
    /** Adds extra damage to each attack. */
    CHARGED,
    /** Explodes around the target. */
    MELON
}