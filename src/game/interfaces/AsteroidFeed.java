package game.interfaces;

import game.api.AsteroidData;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Supplies asteroid data for a game-state-dependent scan window.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public interface AsteroidFeed {

    /**
     * Fetches asteroid observations for the given dates.
     *
     * @param startDate first date of the scan
     * @param endDate last date of the scan
     * @return parsed asteroid observations
     * @throws IOException if the request or response fails
     * @throws InterruptedException if the request is interrupted
     */
    List<AsteroidData> fetch(
            LocalDate startDate,
            LocalDate endDate
    ) throws IOException, InterruptedException;
}
