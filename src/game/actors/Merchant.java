package game.actors;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.WanderBehaviour;

public class Merchant extends Actor
{
    private final Behaviour<Actor, Action> behaviour = new WanderBehaviour();

    public Merchant()
    {
        super("Merchant", 'M', 100);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction,
                           GameMap map, Display display)
    {
        Action action = behaviour.operate(this, map.locationOf(this));

        if (action != null)
        {
            return action;
        }

        return new DoNothingAction();
    }
}
