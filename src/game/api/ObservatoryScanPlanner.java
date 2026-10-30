package game.api;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Generates NASA asteroid scan dates using the current game state.
 *
 * <p>The scan window depends on the Farmer's stamina,
 * map coordinates, and completed observatory scans.</p>
 *
 * @author Chin Kai Xuan 35730374
 * @version 1.0.0
 */
public final class ObservatoryScanPlanner {

    private static final long HISTORY_DAYS = 60L;

    private final Clock clock;

    /**
     * Creates a scan planner using the current UTC date.
     */
    public ObservatoryScanPlanner() {
        this(Clock.systemUTC());
    }

    /**
     * Creates a scan planner with a supplied clock.
     *
     * @param clock the clock used to determine the current date
     */
    public ObservatoryScanPlanner(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Calculates a two-day NASA query window using game state.
     *
     * @param stamina the Farmer's current stamina
     * @param playerX the Farmer's X coordinate
     * @param playerY the Farmer's Y coordinate
     * @param completedScans number of completed observatory scans
     * @return the calculated scan date window
     */
    public ScanWindow plan(int stamina, int playerX,
                           int playerY, int completedScans) {

        if (stamina < 0 || playerX < 0
                || playerY < 0 || completedScans < 0) {
            throw new IllegalArgumentException(
                    "Game-state values cannot be negative"
            );
        }

        long scanSignature = (long) stamina * 3
                + (long) playerX * 5
                + (long) playerY * 11
                + (long) completedScans * 7;

        long offsetDays = Math.floorMod(
                scanSignature, HISTORY_DAYS
        );

        LocalDate endDate = LocalDate.now(clock)
                .minusDays(1 + offsetDays);

        LocalDate startDate = endDate.minusDays(1);

        return new ScanWindow(startDate, endDate);
    }

    /**
     * Stores the start and end dates for a NASA request.
     *
     * @param startDate first date of the request
     * @param endDate last date of the request
     */
    public record ScanWindow(LocalDate startDate, LocalDate endDate) {

        /**
         * Validates the scan window.
         */
        public ScanWindow {
            Objects.requireNonNull(startDate, "startDate");
            Objects.requireNonNull(endDate, "endDate");

            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException(
                        "End date cannot precede start date"
                );
            }
        }
    }
}
