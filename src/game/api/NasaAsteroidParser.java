package game.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Converts NASA NeoWs JSON responses into asteroid data objects.
 *
 * <p>This class is responsible only for parsing API responses.
 * It does not make HTTP requests or modify the game world.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class NasaAsteroidParser {

    /**
     * Parses the asteroid feed returned by NASA NeoWs.
     *
     * @param json the JSON response received from NASA
     * @return an immutable list of parsed asteroid data
     * @throws IOException if the JSON is invalid or required data is missing
     */
    public List<AsteroidData> parseFeed(String json) throws IOException {
        if (json == null || json.isBlank()) {
            throw new IOException("NASA response is empty");
        }

        try {
            JsonElement document = JsonParser.parseString(json);

            if (!document.isJsonObject()) {
                throw new IOException("NASA response must be a JSON object");
            }

            JsonObject root = document.getAsJsonObject();
            JsonObject dateGroups = requireObject(root, "near_earth_objects");

            List<String> dates = new ArrayList<>(dateGroups.keySet());
            Collections.sort(dates);

            List<AsteroidData> asteroids = new ArrayList<>();

            for (String date : dates) {
                LocalDate approachDate = LocalDate.parse(date);
                JsonArray dailyAsteroids = requireArray(dateGroups, date);

                for (JsonElement element : dailyAsteroids) {
                    if (!element.isJsonObject()) {
                        throw new IOException("Invalid asteroid entry");
                    }

                    asteroids.add(
                            parseAsteroid(element.getAsJsonObject(), approachDate)
                    );
                }
            }

            return List.copyOf(asteroids);

        } catch (JsonParseException | IllegalArgumentException
                 | DateTimeParseException exception) {
            throw new IOException(
                    "Unable to parse NASA asteroid response",
                    exception
            );
        }
    }

    /**
     * Converts one NASA asteroid entry into an AsteroidData object.
     *
     * @param asteroid the asteroid JSON object
     * @param date the date associated with the asteroid entry
     * @return the parsed asteroid data
     * @throws IOException if required information is missing
     */
    private AsteroidData parseAsteroid(JsonObject asteroid, LocalDate date)
            throws IOException {

        String id = requireText(asteroid, "id");
        String name = requireText(asteroid, "name");

        boolean hazardous = requireBoolean(
                asteroid, "is_potentially_hazardous_asteroid"
        );

        JsonObject diameter = requireObject(
                requireObject(asteroid, "estimated_diameter"),
                "meters"
        );

        double minimumDiameter = requireNumber(
                diameter, "estimated_diameter_min"
        );
        double maximumDiameter = requireNumber(
                diameter, "estimated_diameter_max"
        );

        if (!Double.isFinite(minimumDiameter)
                || !Double.isFinite(maximumDiameter)
                || minimumDiameter <= 0
                || maximumDiameter < minimumDiameter) {
            throw new IOException("Invalid asteroid diameter estimates");
        }

        double averageDiameter = minimumDiameter
                + (maximumDiameter - minimumDiameter) / 2.0;

        JsonArray approaches = requireArray(
                asteroid, "close_approach_data"
        );

        for (JsonElement element : approaches) {
            if (!element.isJsonObject()) {
                throw new IOException("Invalid close approach entry");
            }

            JsonObject approach = element.getAsJsonObject();

            String approachDate = requireText(
                    approach, "close_approach_date"
            );

            if (!date.toString().equals(approachDate)) {
                continue;
            }

            double velocity = requireNumber(
                    requireObject(approach, "relative_velocity"),
                    "kilometers_per_second"
            );

            double missDistance = requireNumber(
                    requireObject(approach, "miss_distance"),
                    "kilometers"
            );

            return new AsteroidData(
                    id,
                    name,
                    averageDiameter,
                    velocity,
                    missDistance,
                    hazardous,
                    date
            );
        }

        throw new IOException(
                "No matching close approach found for asteroid " + id
        );
    }

    /**
     * Retrieves a required JSON field.
     *
     * @param object the containing JSON object
     * @param field the required field name
     * @return the JSON element
     * @throws IOException if the field is missing or null
     */
    private JsonElement requireField(JsonObject object, String field)
            throws IOException {

        JsonElement element = object.get(field);

        if (element == null || element.isJsonNull()) {
            throw new IOException("Missing NASA field: " + field);
        }

        return element;
    }

    /**
     * Retrieves a required nested JSON object.
     *
     * @param object the containing JSON object
     * @param field the required field name
     * @return the nested JSON object
     * @throws IOException if the field is not an object
     */
    private JsonObject requireObject(JsonObject object, String field)
            throws IOException {

        JsonElement element = requireField(object, field);

        if (!element.isJsonObject()) {
            throw new IOException("Expected JSON object: " + field);
        }

        return element.getAsJsonObject();
    }

    /**
     * Retrieves a required JSON array.
     *
     * @param object the containing JSON object
     * @param field the required field name
     * @return the JSON array
     * @throws IOException if the field is not an array
     */
    private JsonArray requireArray(JsonObject object, String field)
            throws IOException {

        JsonElement element = requireField(object, field);

        if (!element.isJsonArray()) {
            throw new IOException("Expected JSON array: " + field);
        }

        return element.getAsJsonArray();
    }

    /**
     * Retrieves a required string value.
     *
     * @param object the containing JSON object
     * @param field the required field name
     * @return the string value
     * @throws IOException if the field is not a primitive value
     */
    private String requireText(JsonObject object, String field)
            throws IOException {

        JsonElement element = requireField(object, field);

        if (!element.isJsonPrimitive()) {
            throw new IOException("Expected JSON value: " + field);
        }

        return element.getAsString();
    }

    /**
     * Retrieves a required numeric value.
     *
     * @param object the containing JSON object
     * @param field the required field name
     * @return the numeric value
     * @throws IOException if the field is not a valid number
     */
    private double requireNumber(JsonObject object, String field)
            throws IOException {

        String value = requireText(object, field);

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid numeric field: " + field, exception);
        }
    }

    /**
     * Retrieves a required boolean value.
     *
     * @param object the containing JSON object
     * @param field the required field name
     * @return the boolean value
     * @throws IOException if the field is not a boolean
     */
    private boolean requireBoolean(JsonObject object, String field)
            throws IOException {

        JsonElement element = requireField(object, field);

        if (!element.isJsonPrimitive()
                || !element.getAsJsonPrimitive().isBoolean()) {
            throw new IOException("Expected boolean field: " + field);
        }

        return element.getAsBoolean();
    }
}