package game.effects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.GroundAbility;
import game.grounds.StarliteSoil;
import game.interfaces.CelestialRefinable;

import java.util.List;

/**
 * A temporary field that restores nearby actors and repairs
 * celestial impact damage when it expires.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class AstralBlessing extends EnvironmentalField {

    private static final int HEALTH_RECOVERY = 4;
    private static final int STAMINA_RECOVERY = 2;

    /**
     * Creates a blessing field with impact-derived size and lifetime.
     *
     * @param radius radius taken from the impact profile
     * @param durationTurns duration taken from the impact profile
     */
    public AstralBlessing(int radius, int durationTurns) {
        super("Astral Blessing", '!', radius, durationTurns);
    }

    /**
     * Recovers health and stamina of conscious actors in the field.
     *
     * @param centre the main meteor impact site
     */
    @Override
    protected void applyFieldEffect(Location centre) {

        for (Location affected : affectedLocations(centre)) {

            if (!affected.containsAnActor()) {
                continue;
            }

            Actor actor = affected.getActor();

            if (!actor.isConscious()) {
                continue;
            }

            actor.heal(HEALTH_RECOVERY);

            if (actor.hasStatistic(ActorStatistics.STAMINA)) {

                actor.modifyStatistic(
                        ActorStatistics.STAMINA,
                        StatisticOperations.INCREASE,
                        STAMINA_RECOVERY
                );
            }
        }
    }

    /**
     * Repairs crater terrain and refines uncollected celestial resources.
     * Other terrain and ordinary items are left unchanged.
     *
     * @param centre the main meteor impact site
     */
    @Override
    protected void onExpiry(Location centre) {

        for (Location affected : affectedLocations(centre)) {

            // Repair only meteor-created impact sites.
            if (affected.getGround().hasAbility(
                    GroundAbility.METEOR_IMPACT_SITE)) {

                affected.setGround(new StarliteSoil());
            }

            // Create a snapshot because items are replaced below.
            for (Item item : List.copyOf(affected.getItems())) {

                item.asCapability(CelestialRefinable.class)
                        .ifPresent(resource -> {

                            affected.removeItem(item);

                            affected.addItem(resource.refine());
                        });
            }
        }
    }
}
