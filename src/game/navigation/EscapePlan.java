package game.navigation;

import edu.monash.fit2099.engine.positions.Exit;

/**
 * Holds separate movement steps for a Cave Mimic and its decoy.
 * The exits are selected before the Mimic enters Escape.
 *
 * @author Surya Muni Peragas 35651628
 * @version 1.0.0
 */
public final class EscapePlan {

    private final Exit mimicStep;
    private final Exit decoyStep;

    /**
     * Creates a plan containing two valid exits with different destinations.
     *
     * @param mimicStep the first movement step for the real Mimic
     * @param decoyStep the exit used to place and direct the decoy
     */
    public EscapePlan(Exit mimicStep, Exit decoyStep) {
        this.mimicStep = mimicStep;
        this.decoyStep = decoyStep;
    }

    /**
     * Returns the real Mimic's planned movement step.
     *
     * @return the real Mimic's movement exit
     */
    public Exit getMimicStep() {
        return mimicStep;
    }

    /**
     * Returns the exit selected for the decoy.
     *
     * @return the decoy's placement and movement exit
     */
    public Exit getDecoyStep() {
        return decoyStep;
    }
}
