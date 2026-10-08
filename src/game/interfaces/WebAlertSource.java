package game.interfaces;

import game.traps.WebAlert;

public interface WebAlertSource {

    WebAlert getLatestAlert();

    boolean clearAlert(long eventId);

}
