package game.events;

import java.util.Objects;

/**
 * Stores the gameplay consequences of a celestial event.
 *
 * <p>The values are calculated from NASA asteroid data by
 * CelestialEvent implementations. They represent fictional
 * gameplay effects rather than real asteroid impact predictions.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class ImpactProfile {

    private final String eventName;
    private final int impactCount;
    private final int radius;
    private final int damage;
    private final int oreCount;
    private final int guardianCount;
    private final int effectDurationTurns;

    /**
     * Creates an impact profile.
     *
     * @param eventName the name of the celestial event
     * @param impactCount the number of impact sites
     * @param radius the impact radius in tiles
     * @param damage the immediate impact damage
     * @param oreCount the number of meteorite ore items
     * @param guardianCount the number of guardians to spawn
     * @param effectDurationTurns the duration of persistent effects
     */
    public ImpactProfile(String eventName, int impactCount,
                         int radius, int damage, int oreCount,
                         int guardianCount, int effectDurationTurns) {

        this.eventName = Objects.requireNonNull(
                eventName, "eventName"
        );

        if (eventName.isBlank()) {
            throw new IllegalArgumentException(
                    "Event name cannot be blank"
            );
        }

        if (impactCount < 0 || radius < 0 || damage < 0
                || oreCount < 0 || guardianCount < 0
                || effectDurationTurns < 0) {
            throw new IllegalArgumentException(
                    "Impact values cannot be negative"
            );
        }

        this.impactCount = impactCount;
        this.radius = radius;
        this.damage = damage;
        this.oreCount = oreCount;
        this.guardianCount = guardianCount;
        this.effectDurationTurns = effectDurationTurns;
    }

    /**
     * @return the celestial event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @return the number of impact sites
     */
    public int getImpactCount() {
        return impactCount;
    }

    /**
     * @return the impact radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * @return the immediate damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the number of meteorite ore items
     */
    public int getOreCount() {
        return oreCount;
    }

    /**
     * @return the number of guardians
     */
    public int getGuardianCount() {
        return guardianCount;
    }

    /**
     * @return the duration of persistent effects
     */
    public int getEffectDurationTurns() {
        return effectDurationTurns;
    }
}