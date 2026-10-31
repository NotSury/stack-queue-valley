package game.api;

import game.interfaces.AsteroidFeed;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Connects NASA HTTP responses to the asteroid feed interface.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class NasaAsteroidFeed implements AsteroidFeed {

    private final NasaAsteroidClient client;
    private final NasaAsteroidParser parser;

    /**
     * Creates a NASA asteroid feed.
     *
     * @param client NASA HTTP client
     * @param parser NASA JSON parser
     */
    public NasaAsteroidFeed(
            NasaAsteroidClient client,
            NasaAsteroidParser parser) {

        this.client = Objects.requireNonNull(client, "client");
        this.parser = Objects.requireNonNull(parser, "parser");
    }

    /**
     * Downloads and parses NASA asteroid data.
     *
     * @param startDate scan start date
     * @param endDate scan end date
     * @return parsed asteroids
     * @throws IOException if retrieval or parsing fails
     * @throws InterruptedException if retrieval is interrupted
     */
    @Override
    public List<AsteroidData> fetch(
            LocalDate startDate,
            LocalDate endDate)
            throws IOException, InterruptedException {

        String json = client.fetchFeed(startDate, endDate);

        return parser.parseFeed(json);
    }
}
