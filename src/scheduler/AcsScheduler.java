package scheduler;

import java.util.List;

/**
 * Adaptive Container Scheduler (ACS).
 *
 * <p>Owner: Member 2.</p>
 *
 * <p><b>Status: TODO stub.</b> Nothing is implemented yet.</p>
 *
 * <h2>What to implement</h2>
 * <ol>
 *   <li>Copy every input process with {@link Process#copy()}.</li>
 *   <li><b>Tier 1:</b> Round Robin with the given quantum. All new processes
 *       start here.</li>
 *   <li><b>Demotion:</b> when a process uses its whole quantum and still has
 *       remaining time, call {@code tier2Queue.add(p, currentTime)}.</li>
 *   <li><b>Tier 2:</b> non-preemptive priority. Poll with
 *       {@code tier2Queue.pollNext(currentTime)} and run the process until it
 *       finishes. It cannot be preempted.</li>
 *   <li><b>Aging:</b> use {@link AgingPolicy}; do not reimplement the formula.</li>
 *   <li>Charge context-switch time when switching to a different process.</li>
 *   <li>Record one {@link Snapshot} per time step.</li>
 *   <li>Set {@code firstRunTime} and {@code finishTime} on the copies.</li>
 * </ol>
 */
public class AcsScheduler implements Scheduler {

    @Override
    public String getName() {
        // TODO (Member 2): return the ACS display name.
        throw new UnsupportedOperationException("Not implemented yet (Member 2)");
    }

    @Override
    public SimulationResult run(List<Process> input, int quantum, int contextSwitch) {
        // TODO (Member 2): implement Tier 1 Round Robin, Tier 2 demotion,
        // non-preemptive Tier 2 execution, context switching, and snapshots.
        throw new UnsupportedOperationException("Not implemented yet (Member 2)");
    }
}
