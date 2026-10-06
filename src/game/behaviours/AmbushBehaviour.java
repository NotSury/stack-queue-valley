package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.navigation.MovementPlanner;
import game.perception.EnvironmentScanner;

public class AmbushBehaviour extends EnvironmentAwareBehaviour{

    private final Behaviour<Actor, Action> attackBehaviour;
    private final int detectionRadius;

    public AmbushBehaviour(EnvironmentScanner scanner, MovementPlanner movementPlanner, Behaviour<Actor, Action> attackBehaviour, int detectionRadius) {
        super(scanner, movementPlanner);
        this.attackBehaviour = attackBehaviour;
        this.detectionRadius = detectionRadius;
    }

    @Override
    public Action operate(Actor actor, Location location) {
        Actor farmer = getScanner().findFarmer(location, detectionRadius);

        if (farmer == null) {
            return null;
        }

        Action attack = attackBehaviour.operate(actor, location);

        if (attack != null) {
            return attack;
        }

        Location farmerLocation = location.map().locationOf(farmer);

        Exit step = getMovementPlanner().findStepTowards(actor, location, farmerLocation);

        if (step == null) {
            return null;
        }

        return new MoveActorAction(step.getDestination(), step.getName());
    }
}
