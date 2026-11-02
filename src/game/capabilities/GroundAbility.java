package game.capabilities;

/**
 * Represents abilities that can be attached to ground types.
 *
 * @author Chin Kai Xuan 35730374
 * @author Surya Muni Peragas 35651628
 * @version 1.2.0
 */
public enum GroundAbility {
    CROP,

    /**
     * Bare Dirt that can be enriched into Soil by cooking Jam
     */
    BARE_DIRT,

    /**
     * Terrain without a crop or built structure.
     * Actor occupancy and existing items are checked separately.
     */
    OPEN_GROUND,

    METEOR_IMPACTABLE,
    METEOR_IMPACT_SITE,
    COSMIC_CROP
}