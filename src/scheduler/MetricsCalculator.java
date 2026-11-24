package scheduler;

/**
 * Calculates and prints the metrics required by Requirement 2.
 *
 * <p>Owner: Member 3.</p>
 *
 * <p><b>Status: TODO stub.</b> Nothing is implemented yet.</p>
 *
 * <p>The formulas live in {@link Process}: waiting, turnaround and response
 * time. This class will only average them and format the table.</p>
 */
public final class MetricsCalculator {

    /**
     * Average waiting time over all processes.
     *
     * @param result a finished simulation
     * @return the average
     */
    public static double averageWaiting(SimulationResult result) {
        // TODO (Member 3): average Process.getWaitingTime() over all processes.
        throw new UnsupportedOperationException("Not implemented yet (Member 3)");
    }

    /**
     * Average turnaround time over all processes.
     *
     * @param result a finished simulation
     * @return the average
     */
    public static double averageTurnaround(SimulationResult result) {
        // TODO (Member 3): average Process.getTurnaroundTime() over all processes.
        throw new UnsupportedOperationException("Not implemented yet (Member 3)");
    }

    /**
     * Prints one table with a row per process and the two averages below it.
     *
     * <p>Columns: Process, Arrival, Finish, Turnaround, Waiting, Response, Burst.</p>
     *
     * @param result a finished simulation
     */
    public static void printTable(SimulationResult result) {
        // TODO (Member 3): print Process, Arrival, Finish, Turnaround, Waiting, Response, Burst.
        throw new UnsupportedOperationException("Not implemented yet (Member 3)");
    }
}
