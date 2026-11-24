package scheduler;

import java.util.List;
import java.util.Objects;

/**
 * A plain-value picture of the scheduler at one time step.
 *
 * <p>Owner: Member 1 (shared base).</p>
 *
 * <p>A snapshot stores values, not live {@link Process} objects. This means the
 * scheduler can keep filling its queues while old snapshots stay unchanged.</p>
 *
 * @param time      the simulation time of this snapshot
 * @param runningId id of the process running at this time, or {@code null} if
 *                  the CPU is idle
 * @param tier1     processes waiting in Tier 1 (Round Robin)
 * @param tier2     processes waiting in Tier 2 (priority)
 * @param event     short description of what happened, for example
 *                  {@code "P1 demoted to Tier 2"}
 */
public record Snapshot(
        int time,
        String runningId,
        List<ProcessState> tier1,
        List<ProcessState> tier2,
        String event) {

    /**
     * Makes defensive copies and turns a {@code null} event into an empty string.
     */
    public Snapshot {
        tier1 = List.copyOf(tier1);
        tier2 = List.copyOf(tier2);
        event = event == null ? "" : event;
    }

    /**
     * The state of one process at one time step.
     *
     * <p>This record is nested inside {@link Snapshot} on purpose: it only exists
     * as part of a snapshot.</p>
     *
     * @param id                process id, for example {@code "P1"}
     * @param remaining         CPU time still needed
     * @param effectivePriority priority used for Tier 2 ordering. For Tier 1
     *                          processes this is the base priority (Round Robin
     *                          does not use it).
     */
    public record ProcessState(String id, int remaining, int effectivePriority) {
        public ProcessState {
            Objects.requireNonNull(id, "id");
        }
    }
}
