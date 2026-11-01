package game.companions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.magic.FarmMagic;
import game.effects.LivingSoilEffect;
import game.magic.ShieldMagic;
import game.grounds.StrawberrySeedling;
import game.grounds.CauliflowerSprout;
import game.grounds.MelonSprout;
import game.interfaces.PlantFactory;

import java.util.Random;
import java.util.List;

/**
 * A magical deer that help the farm through
 * Living Soil and Nature's Sanctuary.
 *
 * @author Vanessa Liau Jia Yee 34599398
 * @version 1.0.0
 */
public class SanctuaryDeer extends MagicalCompanion {

    private final FarmMagic shieldMagic = new ShieldMagic();
    private final Random random = new Random();
    private final List<PlantFactory> regrowth = List.<PlantFactory>of(
            StrawberrySeedling::new,
            CauliflowerSprout::new,
            MelonSprout::new
    );

    /**
     * Creates a PrismFox with 20 hit points.
     */
    public SanctuaryDeer()
    {
        super("SanctuaryDeer", 'D', 20);
    }

    /**
     * Returns the deer's magical ability.
     *
     * @return the Nature's Sanctuary magic ability
     */
    @Override
    protected FarmMagic getFarmMagic()
    {
        return shieldMagic;
    }

    /**
     * Returns the name of SanctuaryDeer's own ability.
     *
     * @return the Living Soil ability name
     */
    @Override
    public String getOwnAbilityName()
    {
        return LivingSoilEffect.NAME;
    }

    /**
     * Activates Living Soil around the SanctuaryDeer.
     *
     * @param farmer the farmer using the ability
     * @param map the game map
     * @return true when Living Soil is activated
     */
    @Override
    public boolean castOwnAbility(Actor farmer, GameMap map) {
        if (isEffectActive(LivingSoilEffect.NAME))
        {
            return false;
        }

        LivingSoilEffect effect = new LivingSoilEffect(map,
                map.locationOf(this), random, regrowth);

        startEffect(effect);

        return true;
    }
}
