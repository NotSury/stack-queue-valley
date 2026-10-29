package game.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Retrieves asteroid data from NASA's Near-Earth Object Web Service.
 *
 * <p>This class handles HTTP communication with NASA.
 * JSON parsing is handled separately to maintain
 * the Single Responsibility Principle.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class NasaAsteroidClient {

    private static final Duration REQUEST_TIMEOUT =
            Duration.ofSeconds(20);

    private final HttpClient httpClient;

    /**
     * Creates an HTTP client with a connection timeout.
     */
    public NasaAsteroidClient() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build());
    }

    /**
     * Creates a NASA client using a supplied HTTP client.
     *
     * @param httpClient the HTTP client used for requests
     */
    public NasaAsteroidClient(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(
                httpClient, "httpClient"
        );
    }

    /**
     * Retrieves NASA asteroid feed data for a date range.
     *
     * @param startDate first requested date, inclusive
     * @param endDate last requested date, inclusive
     * @return the JSON response from NASA
     * @throws IOException if the request or response fails
     * @throws InterruptedException if the request is interrupted
     * @throws IllegalArgumentException if the date range is invalid
     * @throws IllegalStateException if the API key is missing
     */
    public String fetchFeed(LocalDate startDate, LocalDate endDate)
            throws IOException, InterruptedException {

        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot precede start date"
            );
        }

        if (endDate.isAfter(startDate.plusDays(6))) {
            throw new IllegalArgumentException(
                    "NASA feed supports at most seven calendar days per request"
            );
        }

        String apiKey = NasaApiConfig.requireApiKey();

        String url = NasaApiConfig.FEED_ENDPOINT
                + "?start_date=" + startDate
                + "&end_date=" + endDate
                + "&api_key=" + URLEncoder.encode(
                apiKey, StandardCharsets.UTF_8
        );

        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(url)
                )
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(
                        StandardCharsets.UTF_8
                )
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "NASA NeoWs request failed (HTTP "
                            + response.statusCode() + ")"
            );
        }

        if (response.body() == null
                || response.body().isBlank()) {
            throw new IOException(
                    "NASA NeoWs returned an empty response"
            );
        }

        return response.body();
    }
}
