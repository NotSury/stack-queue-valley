package game.events;

import game.api.AsteroidData;
import game.effects.AstralBlessing;
import game.effects.CosmicRadiation;
import game.effects.EnvironmentalField;
import game.effects.GravitationalDistortion;
import game.interfaces.CelestialEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests NASA-derived celestial event classification,
 * impact calculations, and environmental field creation.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class CelestialEventTest {

    private final List<CelestialEvent> events = List.of(
            new MeteorShower(),
            new AsteroidFragment(),
            new CelestialStorm()
    );

    /**
     * Creates deterministic asteroid data for testing.
     *
     * @param diameter asteroid diameter in metres
     * @param velocity asteroid velocity in km/s
     * @param hazardous NASA hazard classification
     * @return test asteroid data
     */
    private static AsteroidData asteroid(
            double diameter,
            double velocity,
            boolean hazardous) {

        return new AsteroidData(
                "TEST-001",
                "Test Asteroid",
                diameter,
                velocity,
                500000,
                hazardous,
                LocalDate.of(2026, 10, 31)
        );
    }

    /**
     * Provides normal and boundary classification cases.
     *
     * @return event classification arguments
     */
    private static Stream<Arguments> classificationCases() {
        return Stream.of(
                Arguments.of(
                        34.0, 16.0, false,
                        MeteorShower.class
                ),
                Arguments.of(
                        79.99, 16.0, false,
                        MeteorShower.class
                ),
                Arguments.of(
                        80.0, 16.0, false,
                        AsteroidFragment.class
                ),
                Arguments.of(
                        30.0, 19.99, true,
                        AsteroidFragment.class
                ),
                Arguments.of(
                        30.0, 20.0, true,
                        CelestialStorm.class
                ),
                Arguments.of(
                        150.0, 35.0, true,
                        CelestialStorm.class
                )
        );
    }

    /**
     * Verifies that exactly one event supports each asteroid.
     *
     * @param diameter asteroid diameter
     * @param velocity asteroid velocity
     * @param hazardous hazard classification
     * @param expectedType expected event implementation
     */
    @ParameterizedTest
    @MethodSource("classificationCases")
    void classifiesAsteroidsWithoutOverlap(
            double diameter,
            double velocity,
            boolean hazardous,
            Class<?> expectedType) {

        AsteroidData data = asteroid(
                diameter, velocity, hazardous
        );

        List<CelestialEvent> matches = events.stream()
                .filter(event -> event.supports(data))
                .toList();

        assertEquals(
                1,
                matches.size(),
                "Exactly one celestial event should match"
        );

        assertEquals(
                expectedType,
                matches.get(0).getClass()
        );
    }

    /**
     * Provides expected impact profile values.
     *
     * @return impact calculation test arguments
     */
    private static Stream<Arguments> profileCases() {
        return Stream.of(
                Arguments.of(
                        new MeteorShower(),
                        asteroid(34, 16, false),
                        "Meteor Shower",
                        3, 1, 9, 2, 0, 3
                ),
                Arguments.of(
                        new AsteroidFragment(),
                        asteroid(120, 12, false),
                        "Asteroid Fragment",
                        1, 2, 16, 3, 0, 2
                ),
                Arguments.of(
                        new CelestialStorm(),
                        asteroid(150, 35, true),
                        "Celestial Storm",
                        3, 3, 38, 3, 2, 4
                )
        );
    }

    /**
     * Verifies the gameplay values calculated from asteroid data.
     *
     * @param event celestial event implementation
     * @param data asteroid measurements
     * @param name expected event name
     * @param impacts expected impact count
     * @param radius expected impact radius
     * @param damage expected damage
     * @param ore expected ore count
     * @param guardians expected guardian count
     * @param duration expected effect duration
     */
    @ParameterizedTest
    @MethodSource("profileCases")
    void calculatesCorrectImpactProfiles(
            CelestialEvent event,
            AsteroidData data,
            String name,
            int impacts,
            int radius,
            int damage,
            int ore,
            int guardians,
            int duration) {

        ImpactProfile profile = event.buildProfile(data);

        assertAll(
                "Impact profile calculations",
                () -> assertEquals(
                        name, profile.getEventName()
                ),
                () -> assertEquals(
                        impacts, profile.getImpactCount()
                ),
                () -> assertEquals(
                        radius, profile.getRadius()
                ),
                () -> assertEquals(
                        damage, profile.getDamage()
                ),
                () -> assertEquals(
                        ore, profile.getOreCount()
                ),
                () -> assertEquals(
                        guardians, profile.getGuardianCount()
                ),
                () -> assertEquals(
                        duration,
                        profile.getEffectDurationTurns()
                )
        );
    }

    /**
     * Provides expected environmental field types.
     *
     * @return field creation test arguments
     */
    private static Stream<Arguments> fieldCases() {
        return Stream.of(
                Arguments.of(
                        new MeteorShower(),
                        asteroid(34, 16, false),
                        AstralBlessing.class,
                        1, 3
                ),
                Arguments.of(
                        new AsteroidFragment(),
                        asteroid(120, 12, false),
                        GravitationalDistortion.class,
                        2, 2
                ),
                Arguments.of(
                        new CelestialStorm(),
                        asteroid(150, 35, true),
                        CosmicRadiation.class,
                        2, 4
                )
        );
    }

    /**
     * Verifies polymorphic environmental field creation.
     *
     * @param event celestial event
     * @param data asteroid data
     * @param expectedType expected field class
     * @param expectedRadius expected field radius
     * @param expectedDuration expected field duration
     */
    @ParameterizedTest
    @MethodSource("fieldCases")
    void createsCorrectEnvironmentalFields(
            CelestialEvent event,
            AsteroidData data,
            Class<?> expectedType,
            int expectedRadius,
            int expectedDuration) {

        ImpactProfile profile = event.buildProfile(data);

        EnvironmentalField first =
                event.createField(profile);

        EnvironmentalField second =
                event.createField(profile);

        assertEquals(expectedType, first.getClass());

        assertEquals(
                expectedRadius,
                first.getRadius()
        );

        assertEquals(
                expectedDuration,
                first.getRemainingTurns()
        );

        assertFalse(
                first.isActive(),
                "A new field must be inactive before placement"
        );

        assertNotSame(
                first,
                second,
                "Each event must create a fresh field"
        );
    }

    /**
     * Tests Meteor Shower duration boundaries.
     *
     * @param diameter asteroid diameter
     * @param expectedDuration expected field duration
     */
    @ParameterizedTest
    @CsvSource({
            "29.99, 2",
            "30.0, 3",
            "60.0, 4"
    })
    void meteorShowerDurationDependsOnDiameter(
            double diameter,
            int expectedDuration) {

        MeteorShower event = new MeteorShower();

        ImpactProfile profile = event.buildProfile(
                asteroid(diameter, 16, false)
        );

        assertEquals(
                expectedDuration,
                profile.getEffectDurationTurns()
        );
    }

    /**
     * Ensures events reject unsupported asteroid types.
     */
    @Test
    void rejectsUnsupportedAsteroids() {

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new MeteorShower().buildProfile(
                                asteroid(150, 35, true)
                        )
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new AsteroidFragment().buildProfile(
                                asteroid(34, 16, false)
                        )
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new CelestialStorm().buildProfile(
                                asteroid(120, 12, false)
                        )
                )
        );
    }

    /**
     * Tests rejection of invalid NASA measurements.
     *
     * @param diameter asteroid diameter
     * @param velocity asteroid velocity
     * @param missDistance asteroid miss distance
     */
    @ParameterizedTest
    @CsvSource({
            "0, 12, 500000",
            "34, -1, 500000",
            "34, 12, -1"
    })
    void rejectsInvalidAsteroidMeasurements(
            double diameter,
            double velocity,
            double missDistance) {

        assertThrows(
                IllegalArgumentException.class,
                () -> new AsteroidData(
                        "TEST",
                        "Invalid Asteroid",
                        diameter,
                        velocity,
                        missDistance,
                        false,
                        LocalDate.of(2026, 10, 31)
                )
        );
    }

    /**
     * Verifies Cosmic Radiation derives damage from the profile.
     */
    @Test
    void radiationDamageDependsOnImpactDamage() {

        CelestialStorm storm = new CelestialStorm();

        ImpactProfile profile = storm.buildProfile(
                asteroid(150, 35, true)
        );

        CosmicRadiation radiation =
                (CosmicRadiation) storm.createField(profile);

        assertEquals(
                9,
                radiation.getRadiationDamage()
        );
    }
}
