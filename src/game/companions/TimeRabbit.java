package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.effects.TemporalFreezeEffect;
import game.magic.FarmMagic;
import game.magic.GrowthMagic;

/**
 * A magical rabbit that manipulates plant growth and time.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class TimeRabbit extends MagicalCompanion
{
    private final FarmMagic growthMagic = new GrowthMagic();

    /**
     * Creates a TimeRabbit.
     */
    public TimeRabbit()
    {
        super("TimeRabbit", 'R', 20);
    }

    /**
     * Returns Growth Magic as the rabbit's FarmMagic ability.
     *
     * @return GrowthMagic
     */
    @Override
    protected FarmMagic getFarmMagic()
    {
        return growthMagic;
    }

    /**
     * Return the rabbit's own ability name.
     *
     * @return Temporal Freeze
     */
    @Override
    public String getOwnAbilityName()
    {
        return TemporalFreezeEffect.NAME;
    }

    /**
     * Activates Temporal Freeze in a 3x3 area centred on TimeRabbit.
     * The effect will last for 3 turns.
     *
     * @param farmer the farmer using the ability
     * @param map the game map
     * @return true if the Temporal Freeze is successfully activated
     */
    @Override
    public boolean castOwnAbility(Actor farmer, GameMap map)
    {
        if (isEffectActive(TemporalFreezeEffect.NAME))
        {
            return false;
        }

        startEffect(new TemporalFreezeEffect(map, map.locationOf(this)));
        return true;
    }
}
