package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.GroundAbility;

/**
 * Represents a crater formed by a celestial impact.
 *
 * <p>An impact crater replaces terrain affected by a meteor.
 * It is marked as a celestial impact site for other reactions
 * and future environmental effects.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class ImpactCrater extends Ground {

    /**
     * Creates an impact crater.
     */
    public ImpactCrater() {
        super('^', "Impact Crater");
        this.enableAbility(GroundAbility.METEOR_IMPACT_SITE);
    }
}
