
package game.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests dynamic NASA scan planning using reproducible game states.
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public class ObservatoryScanPlannerTest {

    private final ObservatoryScanPlanner planner =
            new ObservatoryScanPlanner(
                    Clock.fixed(
                            Instant.parse("2026-10-31T00:00:00Z"),
                            ZoneOffset.UTC
                    )
            );

    /**
     * Verifies NASA scan dates for different game states.
     *
     * @param stamina current Farmer stamina
     * @param x Farmer X coordinate
     * @param y Farmer Y coordinate
     * @param scans completed Observatory scans
     * @param expectedStart expected start date
     * @param expectedEnd expected end date
     */
    @ParameterizedTest
    @CsvSource({
            "20, 4, 3, 0, 2026-09-06, 2026-09-07",
            "19, 4, 3, 0, 2026-09-09, 2026-09-10",
            "20, 5, 3, 0, 2026-09-01, 2026-09-02",
            "20, 4, 3, 1, 2026-10-29, 2026-10-30"
    })
    void gameStateProducesExpectedScanDates(
            int stamina,
            int x,
            int y,
            int scans,
            String expectedStart,
            String expectedEnd) {

        var window = planner.plan(stamina, x, y, scans);

        assertEquals(
                LocalDate.parse(expectedStart),
                window.startDate()
        );

        assertEquals(
                LocalDate.parse(expectedEnd),
                window.endDate()
        );
    }

    /**
     * Verifies rejection of invalid game-state values.
     *
     * @param stamina current stamina
     * @param x map X coordinate
     * @param y map Y coordinate
     * @param scans completed scan count
     */
    @ParameterizedTest
    @CsvSource({
            "-1, 4, 3, 0",
            "20, -1, 3, 0",
            "20, 4, -1, 0",
            "20, 4, 3, -1"
    })
    void negativeGameStateIsRejected(
            int stamina,
            int x,
            int y,
            int scans) {

        assertThrows(
                IllegalArgumentException.class,
                () -> planner.plan(stamina, x, y, scans)
        );
    }

    /**
     * Verifies identical input produces identical output.
     */
    @Test
    void sameGameStateProducesSameScanWindow() {

        var first = planner.plan(20, 4, 3, 0);
        var second = planner.plan(20, 4, 3, 0);

        assertEquals(first, second);
    }
}
