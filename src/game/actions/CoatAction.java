package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.coatings.Coating;
import game.interfaces.Coatable;
import game.interfaces.CoatingSource;

public class CoatAction extends Action {

    private final Coatable coatable;
    private final CoatingSource source;
    private final Location sourceLocation;

    public CoatAction(Coatable coatable, CoatingSource source, Location sourceLocation){
        this.coatable = coatable;
        this.source = source;
        this.sourceLocation = sourceLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map){
        Coating coating = source.getCoating();
        coatable.setCoating(coating);
        source.onCoatingApplied(actor, sourceLocation);
        return actor + " coats " + coatable + " with " + coating + ".";
    }

    @Override
    public String menuDescription(Actor actor){
        return actor + " coats " + coatable + " with " + source.getCoating();
    }

}
