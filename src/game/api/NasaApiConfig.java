package game.api;

/**
 * Provides configuration for NASA's Near-Earth Object Web Service (NeoWs).
 *
 * <p>The API key is read from the {@code NASA_API_KEY} environment variable
 * so that credentials are not embedded in source code or committed to Git.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class NasaApiConfig {

    /** The endpoint used to retrieve near-Earth objects by approach date. */
    public static final String FEED_ENDPOINT =
            "https://api.nasa.gov/neo/rest/v1/feed";

    /** Prevents instances of this utility class from being created. */
    private NasaApiConfig() {
    }

    /**
     * Returns the API key supplied through the process environment.
     *
     * @return the configured NASA API key
     * @throws IllegalStateException if the key is missing or blank
     */
    public static String requireApiKey() {
        String apiKey = System.getenv("NASA_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "NASA_API_KEY is missing. Set it as an environment variable "
                            + "before running the NASA feature."
            );
        }

        return apiKey.trim();
    }
}
