package scheduler;

import java.util.List;

/**
 * The Non-Preemptive Priority queue used by ACS Tier 2.
 *
 * <p>Owner: Member 2.</p>
 *
 * <p><b>Status: TODO stub.</b> Nothing is implemented yet.</p>
 *
 * <p><b>Tie-break rule to implement:</b> effective priority, then
 * {@code tier2EntryTime}, then id.</p>
 */
public class Tier2Queue {

    /**
     * Adds a process to Tier 2 and records when it entered.
     *
     * @param p           the process being demoted
     * @param currentTime the current simulation time
     */
    public void add(Process p, int currentTime) {
        // TODO (Member 2): set tier = 2, tier2EntryTime = currentTime, and add to the queue.
        throw new UnsupportedOperationException("Not implemented yet (Member 2)");
    }

    /**
     * Removes and returns the process that should run next.
     *
     * @param currentTime the current simulation time, used by the aging rule
     * @return the best process, or {@code null} if the queue is empty
     */
    public Process pollNext(int currentTime) {
        // TODO (Member 2): return the best process by effective priority, then tier2EntryTime, then id.
        throw new UnsupportedOperationException("Not implemented yet (Member 2)");
    }

    /** @return {@code true} if no process is waiting in Tier 2 */
    public boolean isEmpty() {
        // TODO (Member 2): return true when the queue has no waiting process.
        throw new UnsupportedOperationException("Not implemented yet (Member 2)");
    }

    /**
     * Returns the waiting processes without removing them, for snapshots.
     *
     * @return a copy of the waiting processes
     */
    public List<Process> getWaiting() {
        // TODO (Member 2): return a copy of the waiting processes for snapshots.
        throw new UnsupportedOperationException("Not implemented yet (Member 2)");
    }
}
