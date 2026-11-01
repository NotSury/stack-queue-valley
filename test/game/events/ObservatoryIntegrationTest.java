
package game.events;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Cow;
import game.actors.Farmer;
import game.actions.ActivateObservatoryAction;
import game.api.AsteroidData;
import game.api.ObservatoryScanPlanner;
import game.effects.AstralBlessing;
import game.effects.CosmicRadiation;
import game.effects.EnvironmentalField;
import game.effects.GravitationalDistortion;
import game.grounds.Dirt;
import game.grounds.ImpactCrater;
import game.grounds.Observatory;
import game.grounds.Rock;
import game.interfaces.AsteroidFeed;
import game.interfaces.CelestialEvent;
import game.reactions.CropMutationReaction;
import game.reactions.GuardianReaction;
import game.reactions.ImpactDamageReaction;
import game.reactions.ResourceReaction;
import game.reactions.TerrainReaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Observatory-to-gameplay workflow without network access.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class ObservatoryIntegrationTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-10-31T00:00:00Z"),
            ZoneOffset.UTC
    );

    /**
     * Records NASA requests and returns controlled responses.
     */
    private static final class FakeFeed implements AsteroidFeed {

        private List<AsteroidData> response;
        private IOException ioFailure;
        private RuntimeException runtimeFailure;
        private boolean interrupted;

        private final List<ObservatoryScanPlanner.ScanWindow> requests =
                new ArrayList<>();

        private FakeFeed(List<AsteroidData> response) {
            this.response = response;
        }

        @Override
        public List<AsteroidData> fetch(
                LocalDate startDate,
                LocalDate endDate)
                throws IOException, InterruptedException {

            requests.add(
                    new ObservatoryScanPlanner.ScanWindow(
                            startDate, endDate)
            );

            if (ioFailure != null) {
                throw ioFailure;
            }

            if (runtimeFailure != null) {
                throw runtimeFailure;
            }

            if (interrupted) {
                throw new InterruptedException(
                        "Simulated interrupted scan"
                );
            }

            return response;
        }
    }

    /**
     * Creates fresh game objects for every test.
     */
    private static final class Fixture {

        private final GameMap map;
        private final Farmer farmer;
        private final Location observatory;
        private final FakeFeed feed;
        private final ObservatoryScanPlanner planner;
        private final CelestialEventManager manager;

        private Fixture(AsteroidData asteroid)
                throws Exception {

            this(
                    List.of(asteroid),
                    List.of(
                            new MeteorShower(),
                            new AsteroidFragment(),
                            new CelestialStorm()
                    )
            );
        }

        private Fixture(
                List<AsteroidData> asteroids,
                List<CelestialEvent> events)
                throws Exception {

            DefaultGroundCreator creator =
                    new DefaultGroundCreator();

            creator.registerGround('.', Dirt::new);

            map = new GameMap(
                    "Observatory Test",
                    creator,
                    '.',
                    7,
                    7
            );

            World world = new World(new Display()) {};
            world.addGameMap(map);

            farmer = new Farmer("Tester");
            map.addActor(farmer, map.at(0, 3));

            observatory = map.at(1, 3);

            feed = new FakeFeed(asteroids);

            planner = new ObservatoryScanPlanner(
                    FIXED_CLOCK
            );

            manager = new CelestialEventManager(
                    feed,
                    planner,
                    events,
                    List.of(
                            new CropMutationReaction(new Random(7)),
                            new TerrainReaction(),
                            new ImpactDamageReaction(),
                            new ResourceReaction(),
                            new GuardianReaction()
                    ),
                    new Random(11)
            );

            observatory.setGround(
                    new Observatory(manager)
            );
        }
    }

    /**
     * Creates reproducible asteroid measurements.
     *
     * @param diameter asteroid diameter
     * @param velocity asteroid velocity
     * @param hazardous hazard classification
     * @return controlled asteroid data
     */
    private static AsteroidData asteroid(
            double diameter,
            double velocity,
            boolean hazardous) {

        return new AsteroidData(
                "TEST-001",
                "Controlled Asteroid",
                diameter,
                velocity,
                500000,
                hazardous,
                LocalDate.of(2026, 10, 30)
        );
    }

    /**
     * Provides the three supported celestial event types.
     *
     * @return test arguments
     */
    private static Stream<Arguments> eventCases() {

        return Stream.of(
                Arguments.of(
                        asteroid(34, 16, false),
                        "Meteor Shower",
                        AstralBlessing.class,
                        3
                ),
                Arguments.of(
                        asteroid(120, 12, false),
                        "Asteroid Fragment",
                        GravitationalDistortion.class,
                        2
                ),
                Arguments.of(
                        asteroid(150, 35, true),
                        "Celestial Storm",
                        CosmicRadiation.class,
                        4
                )
        );
    }

    /**
     * Verifies NASA events change the game world
     * and consume the correct amount of stamina.
     *
     * @param asteroid asteroid to process
     * @param expectedName expected event name
     * @param expectedField expected environmental field
     * @param expectedDuration field duration
     * @throws Exception if initialization fails
     */
    @ParameterizedTest
    @MethodSource("eventCases")
    void scansProduceCorrectGameplayEffects(
            AsteroidData asteroid,
            String expectedName,
            Class<? extends EnvironmentalField> expectedField,
            int expectedDuration)
            throws Exception {

        Fixture fixture = new Fixture(asteroid);

        Rock protectedRock = new Rock();
        fixture.map.at(2, 3).setGround(protectedRock);

        int initialStamina = fixture.farmer.getStatistic(
                ActorStatistics.STAMINA
        );

        String result = fixture.manager.scan(
                fixture.farmer,
                fixture.observatory
        );

        assertTrue(result.contains(expectedName));

        assertEquals(
                1,
                fixture.feed.requests.size()
        );

        assertEquals(
                fixture.planner.plan(initialStamina, 0, 3, 0),
                fixture.feed.requests.get(0)
        );

        assertEquals(
                initialStamina - 3,
                fixture.farmer.getStatistic(
                        ActorStatistics.STAMINA)
        );

        assertEquals(
                1,
                fixture.manager.getCompletedScans()
        );

        assertFalse(fixture.manager.canScan());

        EnvironmentalField field =
                fixture.manager.getActiveField().orElseThrow();

        assertInstanceOf(expectedField, field);

        assertEquals(
                expectedDuration,
                field.getRemainingTurns()
        );

        assertTrue(
                field.getCentre().getItems().contains(field)
        );

        assertSame(
                protectedRock,
                fixture.map.at(2, 3).getGround()
        );

        assertInstanceOf(
                Observatory.class,
                fixture.observatory.getGround()
        );

        long craters = 0;
        long fields = 0;

        for (int x : fixture.map.getXRange()) {
            for (int y : fixture.map.getYRange()) {

                Location location = fixture.map.at(x, y);

                if (location.getGround()
                        instanceof ImpactCrater) {
                    craters++;
                }

                fields += location.getItems()
                        .stream()
                        .filter(
                                EnvironmentalField.class::isInstance
                        )
                        .count();
            }
        }

        assertTrue(
                craters > 0,
                "At least one meteor crater is expected"
        );

        assertEquals(
                1L,
                fields,
                "One persistent field per scan"
        );
    }

    /**
     * Tests Observatory actions, cooldown and dynamic dates.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void actionCooldownAndNewScanDatesWorkAfterFieldExpires()
            throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        var actions =
                fixture.observatory.getGround().allowableActions(
                        fixture.farmer,
                        fixture.observatory,
                        "East"
                );

        assertEquals(1, actions.size());

        Action action = actions.get(0);

        assertInstanceOf(
                ActivateObservatoryAction.class,
                action
        );

        assertTrue(
                action.execute(fixture.farmer, fixture.map)
                        .contains("Meteor Shower")
        );

        assertEquals(
                0,
                fixture.observatory.getGround().allowableActions(
                        fixture.farmer,
                        fixture.observatory,
                        "East"
                ).size()
        );

        int staminaAfterFirstScan =
                fixture.farmer.getStatistic(
                        ActorStatistics.STAMINA
                );

        String blocked = fixture.manager.scan(
                fixture.farmer,
                fixture.observatory
        );

        assertTrue(
                blocked.contains("field is active")
        );

        assertEquals(
                1,
                fixture.feed.requests.size()
        );

        assertEquals(
                staminaAfterFirstScan,
                fixture.farmer.getStatistic(
                        ActorStatistics.STAMINA)
        );

        int ticks = 0;

        while (!fixture.manager.canScan() && ticks < 10) {
            fixture.map.tick();
            ticks++;
        }

        assertEquals(
                3,
                ticks,
                "Meteor Shower blessing should expire after three ticks"
        );

        assertTrue(
                fixture.manager.getActiveField().isEmpty()
        );

        assertEquals(
                1,
                fixture.observatory.getGround().allowableActions(
                        fixture.farmer,
                        fixture.observatory,
                        "East"
                ).size()
        );

        assertTrue(
                fixture.manager.scan(
                        fixture.farmer,
                        fixture.observatory
                ).contains("Meteor Shower")
        );

        assertEquals(
                2,
                fixture.manager.getCompletedScans()
        );

        assertEquals(
                2,
                fixture.feed.requests.size()
        );

        assertNotEquals(
                fixture.feed.requests.get(0),
                fixture.feed.requests.get(1),
                "Game-state changes must change the NASA date window"
        );
    }

    /**
     * Possible simulated NASA failures.
     */
    private enum FailureMode {
        IO_ERROR,
        RUNTIME_ERROR,
        EMPTY_RESPONSE,
        NULL_RESPONSE
    }

    /**
     * Tests unsuccessful API responses without changing game state.
     *
     * @param failure simulated failure
     * @throws Exception if initialization fails
     */
    @ParameterizedTest
    @EnumSource(FailureMode.class)
    void failedFeedLeavesGameStateUnchanged(
            FailureMode failure)
            throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        switch (failure) {

            case IO_ERROR ->
                    fixture.feed.ioFailure =
                            new IOException("Offline");

            case RUNTIME_ERROR ->
                    fixture.feed.runtimeFailure =
                            new IllegalStateException(
                                    "Invalid NASA response"
                            );

            case EMPTY_RESPONSE ->
                    fixture.feed.response = List.of();

            case NULL_RESPONSE ->
                    fixture.feed.response = null;
        }

        String result = fixture.manager.scan(
                fixture.farmer,
                fixture.observatory
        );

        assertTrue(
                result.contains("No stamina was spent")
        );

        assertEquals(
                20,
                fixture.farmer.getStatistic(
                        ActorStatistics.STAMINA)
        );

        assertEquals(
                0,
                fixture.manager.getCompletedScans()
        );

        assertTrue(fixture.manager.canScan());

        assertTrue(
                fixture.manager.getActiveField().isEmpty()
        );

        assertEquals(
                1,
                fixture.feed.requests.size()
        );

        assertInstanceOf(
                Dirt.class,
                fixture.map.at(0, 3).getGround()
        );
    }

    /**
     * Checks that insufficient stamina prevents API requests.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void insufficientStaminaDoesNotContactFeed()
            throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        fixture.farmer.modifyStatistic(
                ActorStatistics.STAMINA,
                StatisticOperations.DECREASE,
                18
        );

        assertTrue(
                fixture.manager.scan(
                        fixture.farmer,
                        fixture.observatory
                ).contains("at least 3 stamina")
        );

        assertEquals(
                2,
                fixture.farmer.getStatistic(
                        ActorStatistics.STAMINA)
        );

        assertEquals(
                0,
                fixture.feed.requests.size()
        );

        assertEquals(
                0,
                fixture.manager.getCompletedScans()
        );
    }

    /**
     * Checks that non-Farmer actors cannot use the Observatory.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void nonFarmerCannotScan() throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        Cow cow = new Cow();

        fixture.map.addActor(
                cow,
                fixture.map.at(0, 4)
        );

        assertTrue(
                fixture.manager.scan(
                        cow,
                        fixture.observatory
                ).contains("Only the Farmer")
        );

        assertEquals(
                0,
                fixture.observatory.getGround().allowableActions(
                        cow,
                        fixture.observatory,
                        "East"
                ).size()
        );

        assertEquals(
                0,
                fixture.feed.requests.size()
        );
    }

    /**
     * Checks that the Farmer must be next to the Observatory.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void distantFarmerCannotScan() throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        fixture.map.moveActor(
                fixture.farmer,
                fixture.map.at(6, 6)
        );

        assertTrue(
                fixture.manager.scan(
                        fixture.farmer,
                        fixture.observatory
                ).contains("Move next")
        );

        assertEquals(
                0,
                fixture.feed.requests.size()
        );
    }

    /**
     * Checks that the scan requires impactable terrain.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void noImpactableTerrainPreventsScan()
            throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        for (int x : fixture.map.getXRange()) {
            for (int y : fixture.map.getYRange()) {

                if (fixture.map.at(x, y)
                        != fixture.observatory) {

                    fixture.map.at(x, y).setGround(
                            new Rock()
                    );
                }
            }
        }

        assertTrue(
                fixture.manager.scan(
                        fixture.farmer,
                        fixture.observatory
                ).contains("No suitable impact terrain")
        );

        assertEquals(
                0,
                fixture.feed.requests.size()
        );

        assertEquals(
                20,
                fixture.farmer.getStatistic(
                        ActorStatistics.STAMINA)
        );
    }

    /**
     * Verifies that an action rejects another game map.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void actionRejectsDifferentMap() throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        DefaultGroundCreator creator =
                new DefaultGroundCreator();

        creator.registerGround('.', Dirt::new);

        GameMap otherMap = new GameMap(
                "Other Map",
                creator,
                '.',
                3,
                3
        );

        Action action =
                fixture.observatory.getGround().allowableActions(
                        fixture.farmer,
                        fixture.observatory,
                        "East"
                ).get(0);

        assertEquals(
                "The Observatory is on another map.",
                action.execute(fixture.farmer, otherMap)
        );

        assertEquals(
                0,
                fixture.feed.requests.size()
        );
    }

    /**
     * Tests invalid asteroid event calculation handling.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void invalidEventProfileDoesNotModifyWorld()
            throws Exception {

        CelestialEvent invalidEvent = new CelestialEvent() {

            @Override
            public boolean supports(AsteroidData data) {
                return true;
            }

            @Override
            public ImpactProfile buildProfile(
                    AsteroidData data) {

                throw new IllegalArgumentException(
                        "Corrupt profile"
                );
            }

            @Override
            public EnvironmentalField createField(
                    ImpactProfile profile) {

                throw new IllegalStateException(
                        "Not expected to execute"
                );
            }
        };

        Fixture fixture = new Fixture(
                List.of(asteroid(34, 16, false)),
                List.of(invalidEvent)
        );

        assertTrue(
                fixture.manager.scan(
                        fixture.farmer,
                        fixture.observatory
                ).contains("No stamina was spent")
        );

        assertEquals(
                0,
                fixture.manager.getCompletedScans()
        );

        assertEquals(
                20,
                fixture.farmer.getStatistic(
                        ActorStatistics.STAMINA)
        );

        assertTrue(
                fixture.manager.getActiveField().isEmpty()
        );

        assertInstanceOf(
                Dirt.class,
                fixture.map.at(0, 3).getGround()
        );
    }

    /**
     * Tests interrupted NASA requests and cleanup.
     *
     * @throws Exception if initialization fails
     */
    @Test
    void interruptedScanPreservesInterruptFlag()
            throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        fixture.feed.interrupted = true;

        try {

            assertTrue(
                    fixture.manager.scan(
                            fixture.farmer,
                            fixture.observatory
                    ).contains("No stamina was spent")
            );

            assertTrue(
                    Thread.currentThread().isInterrupted()
            );

            assertEquals(
                    20,
                    fixture.farmer.getStatistic(
                            ActorStatistics.STAMINA)
            );

            assertEquals(
                    0,
                    fixture.manager.getCompletedScans()
            );

        } finally {

            // Prevent interference with subsequent JUnit tests.
            Thread.interrupted();
        }
    }

/**
 * Verifies that repeated scans can reuse existing impact
 * craters instead of exhausting nearby impactable terrain.
 *
 * @throws Exception if the test environment cannot initialize
 */

 @Test
    void repeatedScansKeepFindingTargets() throws Exception {

        Fixture fixture = new Fixture(
                asteroid(400, 10, false)
        );

        for (int scan = 0; scan < 5; scan++) {

            fixture.farmer.modifyStatistic(
                    ActorStatistics.STAMINA,
                    StatisticOperations.INCREASE,
                    20
            );

            String result = fixture.manager.scan(
                    fixture.farmer,
                    fixture.observatory
            );

            assertTrue(
                    result.contains("triggered"),
                    "Scan " + (scan + 1)
                            + " failed: " + result
            );

            int ticks = 0;

            while (!fixture.manager.canScan()
                    && ticks < 20) {

                fixture.map.tick();
                ticks++;
            }

            assertTrue(
                    fixture.manager.canScan(),
                    "Environmental field did not expire"
            );
        }

        assertEquals(
                5,
                fixture.manager.getCompletedScans()
        );
    }

    /**
     * Verifies that the Observatory cannot be entered while
     * its scan action remains available to the Farmer.
     *
     * @throws Exception if test initialization fails
     */
    @Test
    void observatoryIsImpassableButScanRemainsAvailable()
            throws Exception {

        Fixture fixture = new Fixture(
                asteroid(34, 16, false)
        );

        var ground = fixture.observatory.getGround();

        // Neither the Farmer nor another actor can enter.
        assertFalse(
                ground.canActorEnter(fixture.farmer)
        );

        assertFalse(
                ground.canActorEnter(new Cow())
        );

        // The Farmer can still use the Observatory.
        var actions = ground.allowableActions(
                fixture.farmer,
                fixture.observatory,
                "East"
        );

        assertEquals(1, actions.size());

        assertInstanceOf(
                ActivateObservatoryAction.class,
                actions.get(0)
        );

        // Other actors cannot activate the Observatory.
        Cow cow = new Cow();

        assertEquals(
                0,
                ground.allowableActions(
                        cow,
                        fixture.observatory,
                        "East"
                ).size()
        );
    }
}