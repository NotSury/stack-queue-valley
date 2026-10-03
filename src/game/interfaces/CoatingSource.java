package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

public interface CoatingSource {

    Coating getCoating();

    default boolean canProvideCoating() {
        return true;
    }

    default void onCoatingApplied(Actor actor, Location sourceLocation) {

    }

}
