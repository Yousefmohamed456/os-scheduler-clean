package scheduler;

import java.util.List;
import java.util.Objects;

/**
 * The result of one scheduler run.
 *
 * <p>Owner: Member 1 (shared base).</p>
 *
 * @param schedulerName display name of the scheduler that produced the result
 * @param processes     the simulated process copies, with finish times filled in
 * @param snapshots     one entry per time step; ACS fills these, SJF may leave
 *                      the list empty
 */
public record SimulationResult(
        String schedulerName,
        List<Process> processes,
        List<Snapshot> snapshots) {

    /**
     * Makes defensive copies of the lists so a result cannot be changed later
     * by accident.
     */
    public SimulationResult {
        Objects.requireNonNull(schedulerName, "schedulerName");
        processes = List.copyOf(processes);
        snapshots = List.copyOf(snapshots);
    }
}
