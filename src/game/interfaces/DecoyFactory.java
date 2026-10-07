package game.interfaces;

import edu.monash.fit2099.engine.actors.Actor;
import game.actors.DecoyMimic;

/**
 * Creates temporary decoys with a supplied appearance and movement direction.
 * The caller is responsible for placing the decoy on a map.
 */
public interface DecoyFactory {

    /**
     * Creates an unplaced decoy that copies an actor's visible appearance.
     *
     * @param original the actor whose appearance is copied
     * @param direction the direction followed during ordinary decoy turns
     * @return the newly created decoy
     */
    DecoyMimic createDecoy(Actor original, String direction);
}
