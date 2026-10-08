package game.navigation;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

public class HazardAwareMovementPlanner implements MovementPlanner{

    private final MovementPlanner basePlanner;

    private final WebPlanner safeRouter;

    public HazardAwareMovementPlanner(MovementPlanner basePlanner, WebPlanner safeRouter) {
        this.basePlanner = basePlanner;
        this.safeRouter = safeRouter;
    }

    @Override
    public Exit findStepTowards(Actor creature, Location origin, Location target){
        return safeRouter.findStepTowards(creature, origin, target);
    }

    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat){
        return basePlanner.findStepAway(creature, origin, threat);
    }

    @Override
    public Exit findStepAway(Actor creature, Location origin, Location threat, Exit excludeExit){
        return basePlanner.findStepAway(creature, origin, threat);
    }

    @Override
    public Exit findStepInDirection(Actor creature,Location origin, String direction){
        return basePlanner.findStepInDirection(creature, origin, direction);
    }

    @Override
    public EscapePlan planEscape(Actor creature, Location origin, Location threat){
        return basePlanner.planEscape(creature, origin, threat);
    }
}

