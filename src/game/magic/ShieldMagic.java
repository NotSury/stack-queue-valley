package game.magic;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.companions.MagicalCompanion;
import game.effects.NatureSanctuaryEffect;

/**
 * Provides Nature's Sanctuary magic to SanctuaryDeer.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.1.0
 */
public class ShieldMagic implements FarmMagic {

    /**
     * Health and stamina restored on activation.
     */
    private static final int RESTORE = 2;

    /**
     * Returns the name of this magical ability.
     *
     * @return the ability name
     */
    @Override
    public String getName() {
        return NatureSanctuaryEffect.NAME;
    }

    /**
     * Activates Nature's Sanctuary, restoring the Farmer's
     * health and stamina and repelling nearby hostile creatures.
     *
     * @param farmer the Farmer activating the magic
     * @param companion the magical companion
     * @param map the game map
     * @return true if the sanctuary activates successfully
     */
    @Override
    public boolean cast(Actor farmer, MagicalCompanion companion, GameMap map) {
        farmer.modifyStatistic(ActorStatistics.HEALTH,
                StatisticOperations.INCREASE,
                RESTORE);

        farmer.modifyStatistic(ActorStatistics.STAMINA,
                StatisticOperations.INCREASE,
                RESTORE);

        Location centre =  map.locationOf(farmer);

        NatureSanctuaryEffect effect = new NatureSanctuaryEffect(map, centre);

        effect.tick();

        return true;
    }
}
