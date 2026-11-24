package scheduler;

/**
 * The aging rule for ACS Tier 2.
 *
 * <p>Owner: Member 3 (Tier 3).</p>
 *
 * <p><b>Status: TODO stub.</b> Nothing is implemented yet.</p>
 *
 * <p><b>Formula to implement:</b></p>
 * <pre>
 * aging             = floor(waitTime / 4)
 * effectivePriority = priority - aging
 * </pre>
 *
 * <p>{@code waitTime} starts when the process enters Tier 2. Lower number =
 * higher priority. Do not clamp the result.</p>
 */
public final class AgingPolicy {

    /** Number of time units of waiting that improve the priority by 1. */
    public static final int AGING_INTERVAL = 4;

    /**
     * Computes the priority used to choose the next Tier 2 process.
     *
     * @param p           the process to check
     * @param currentTime the current simulation time
     * @return the base priority if the process is not in Tier 2; otherwise the
     *         aged effective priority
     */
    public static int getEffectivePriority(Process p, int currentTime) {
        // TODO (Member 3): return priority - floor((currentTime - tier2EntryTime) / 4),
        // or the base priority if the process has not entered Tier 2.
        throw new UnsupportedOperationException("Not implemented yet (Member 3)");
    }
}
