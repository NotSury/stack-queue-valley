package game.events;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import game.api.AsteroidData;
import game.api.ObservatoryScanPlanner;
import game.capabilities.ActorRole;
import game.capabilities.GroundAbility;
import game.effects.EnvironmentalField;
import game.interfaces.AsteroidFeed;
import game.interfaces.CelestialEvent;
import game.interfaces.CelestialReaction;
import game.interfaces.CelestialScanService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Coordinates NASA scans, celestial events, impact reactions,
 * and temporary environmental fields.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class CelestialEventManager
        implements CelestialScanService {

    private static final int SCAN_STAMINA_COST = 3;
    private static final int IMPACT_SEARCH_DISTANCE = 4;

    private final AsteroidFeed feed;
    private final ObservatoryScanPlanner planner;
    private final List<CelestialEvent> events;
    private final List<CelestialReaction> reactions;
    private final Random random;

    private EnvironmentalField activeField;
    private int completedScans;

    /**
     * Creates a manager with injected dependencies.
     *
     * @param feed asteroid data provider
     * @param planner dynamic NASA scan planner
     * @param events supported celestial event strategies
     * @param reactions ordered impact reactions
     * @param random random source for event and impact selection
     */
    public CelestialEventManager(
            AsteroidFeed feed,
            ObservatoryScanPlanner planner,
            List<CelestialEvent> events,
            List<CelestialReaction> reactions,
            Random random) {

        this.feed = Objects.requireNonNull(feed, "feed");
        this.planner = Objects.requireNonNull(planner, "planner");
        this.events = List.copyOf(events);
        this.reactions = List.copyOf(reactions);
        this.random = Objects.requireNonNull(random, "random");

        if (events.isEmpty() || reactions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Events and reactions must not be empty"
            );
        }
    }

    /**
     * Checks whether an existing environmental field has expired.
     *
     * @return true when another scan may begin
     */
    @Override
    public boolean canScan() {
        return activeField == null || !activeField.isActive();
    }

    /**
     * Returns the number of successful scans.
     *
     * @return completed scans
     */
    public int getCompletedScans() {
        return completedScans;
    }

    /**
     * Returns the currently active environmental field, if any.
     *
     * @return active field
     */
    public Optional<EnvironmentalField> getActiveField() {
        return canScan()
                ? Optional.empty()
                : Optional.of(activeField);
    }

    /**
     * Activates a NASA-driven celestial event.
     *
     * @param actor actor using the Observatory
     * @param observatory location of the Observatory
     * @return scan and gameplay result
     */
    @Override
    public String scan(Actor actor, Location observatory) {

        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(observatory, "observatory");

        if (!actor.hasAbility(ActorRole.FARMER)) {
            return "Only the Farmer can activate the Observatory.";
        }

        GameMap map = observatory.map();

        if (!map.contains(actor)) {
            return "The Farmer must be on the Observatory's map.";
        }

        Location farmerLocation = map.locationOf(actor);

        if (Math.abs(farmerLocation.x() - observatory.x()) > 1
                || Math.abs(farmerLocation.y() - observatory.y()) > 1) {

            return "Move next to the Observatory before scanning.";
        }

        if (!canScan()) {
            return "The Observatory cannot scan while "
                    + "an environmental field is active.";
        }

        if (!actor.hasStatistic(ActorStatistics.STAMINA)
                || actor.getStatistic(ActorStatistics.STAMINA)
                < SCAN_STAMINA_COST) {

            return "You need at least "
                    + SCAN_STAMINA_COST
                    + " stamina to scan.";
        }

        List<Location> candidates = impactCandidates(observatory);

        if (candidates.isEmpty()) {
            return "No suitable impact terrain is close enough "
                    + "to the Observatory.";
        }

        // Game-state-dependent NASA query parameters.
        var window = planner.plan(
                actor.getStatistic(ActorStatistics.STAMINA),
                farmerLocation.x(),
                farmerLocation.y(),
                completedScans
        );

        List<AsteroidData> asteroids;

        try {
            asteroids = feed.fetch(
                    window.startDate(),
                    window.endDate()
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            return "NASA scan interrupted. No stamina was spent.";

        } catch (IOException | RuntimeException exception) {

            return "NASA is unavailable or returned invalid data. "
                    + "No stamina was spent.";
        }

        if (asteroids == null || asteroids.isEmpty()) {
            return "NASA reported no asteroids for this scan "
                    + "window. No stamina was spent.";
        }

        // Validate and prepare the event before modifying game state.
        AsteroidData asteroid;
        CelestialEvent selected;
        ImpactProfile profile;
        EnvironmentalField field;

        try {
            List<AsteroidData> supported = asteroids.stream()
                    .filter(Objects::nonNull)
                    .filter(data -> events.stream()
                            .anyMatch(event -> event.supports(data)))
                    .toList();

            if (supported.isEmpty()) {
                return "No supported celestial event was found. "
                        + "No stamina was spent.";
            }

            asteroid = supported.get(
                    random.nextInt(supported.size())
            );

            selected = events.stream()
                    .filter(event -> event.supports(asteroid))
                    .findFirst()
                    .orElseThrow();

            profile = selected.buildProfile(asteroid);

            field = Objects.requireNonNull(
                    selected.createField(profile),
                    "Celestial event created no field"
            );

        } catch (RuntimeException exception) {

            return "NASA asteroid data could not produce a valid "
                    + "celestial event. No stamina was spent.";
        }

        // Reset reaction state once per celestial event,
        // not once per individual impact location.
        for (CelestialReaction reaction : reactions) {
            reaction.beginEvent();
        }

        // Select valid impact sites near the Observatory.
        Collections.shuffle(candidates, random);

        int impacts = Math.min(
                profile.getImpactCount(),
                candidates.size()
        );

        // Reactions are applied in the injected order.
        for (int i = 0; i < impacts; i++) {

            Location centre = candidates.get(i);

            for (CelestialReaction reaction : reactions) {

                reaction.apply(profile, centre);

                removeUnconsciousActors(
                        centre,
                        profile.getRadius()
                );
            }
        }

        // One persistent environmental field per celestial event.
        field.placeAt(candidates.get(0));
        activeField = field;

        completedScans++;

        actor.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                SCAN_STAMINA_COST
        );

        return "NASA scan (" + window.startDate()
                + " to " + window.endDate()
                + ") triggered " + profile.getEventName()
                + " at " + impacts + " location(s). "
                + field + " is active for "
                + field.getRemainingTurns()
                + " turns. Scan cost: "
                + SCAN_STAMINA_COST + " stamina.";
    }

    /**
     * Removes unconscious actors through their own death hooks.
     * This preserves specialised effects such as ore drops.
     *
     * @param centre centre of the impact
     * @param radius impact radius
     */
    private void removeUnconsciousActors(
            Location centre,
            int radius) {

        GameMap map = centre.map();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                long dx = (long) x - centre.x();
                long dy = (long) y - centre.y();

                if (dx * dx + dy * dy
                        <= (long) radius * radius) {

                    Location affected = map.at(x, y);

                    if (affected.containsAnActor()
                            && !affected.getActor().isConscious()) {

                        affected.getActor().unconscious(map);
                    }
                }
            }
        }
    }

    /**
     * Finds meteor-impactable terrain near the Observatory.
     *
     * @param observatory location of the Observatory
     * @return suitable impact locations
     */
    private List<Location> impactCandidates(Location observatory) {

        List<Location> result = new ArrayList<>();
        GameMap map = observatory.map();

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {

                if (Math.abs(x - observatory.x())
                        <= IMPACT_SEARCH_DISTANCE
                        && Math.abs(y - observatory.y())
                        <= IMPACT_SEARCH_DISTANCE) {

                    Location candidate = map.at(x, y);

                    if (candidate.getGround() != null
                            && candidate.getGround().hasAbility(
                            GroundAbility.METEOR_IMPACTABLE)) {

                        result.add(candidate);
                    }
                }
            }
        }

        return result;
    }
}
