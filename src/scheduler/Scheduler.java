package scheduler;

import java.util.List;

/**
 * Common interface for both schedulers.
 *
 * <p>Owner: Member 1 (shared base).</p>
 *
 * <h2>Rules that every implementation must follow</h2>
 * <ul>
 *   <li><b>Never modify the input.</b> Do not change the {@code input} list or
 *       the {@link Process} objects inside it. Start by copying every process
 *       with {@link Process#copy()} and simulate the copies.</li>
 *   <li><b>Record the important times.</b> Set
 *       {@link Process#setFirstRunTime(int)} the first time a process runs and
 *       {@link Process#setFinishTime(int)} when it finishes.</li>
 *   <li><b>Count context switches.</b> Context-switch time must be counted in
 *       every preemptive algorithm.</li>
 *   <li><b>SJF ignores the quantum.</b> The {@code quantum} parameter is only
 *       used by the ACS Tier 1 Round Robin queue.</li>
 * </ul>
 */
public interface Scheduler {

    /**
     * @return a short display name, for example {@code "Preemptive SJF"}
     */
    String getName();

    /**
     * Runs one simulation.
     *
     * @param input         the test-case processes; this list and its objects
     *                      must not be modified
     * @param quantum       Round Robin quantum (ignored by SJF)
     * @param contextSwitch time units charged for each context switch
     * @return the finished process copies plus the recorded snapshots
     */
    SimulationResult run(List<Process> input, int quantum, int contextSwitch);
}
