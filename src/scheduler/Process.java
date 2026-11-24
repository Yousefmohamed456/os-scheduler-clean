package scheduler;

import java.util.Objects;

/**
 * One process in the simulation.
 *
 * <p>Owner: Member 1 (shared base).</p>
 *
 * <p>The four input values ({@code id}, {@code arrivalTime}, {@code burstTime},
 * {@code priority}) never change. The other fields are simulation state and are
 * filled in by the scheduler while it runs.</p>
 *
 * <p><b>Note about the class name:</b> this class is called {@code Process} and
 * therefore hides {@link java.lang.Process} inside the {@code scheduler} package.
 * That is intentional and harmless for this project. If you ever need the JDK
 * class, write its full name: {@code java.lang.Process}.</p>
 */
public class Process {

    // ---------- Input data (fixed for the whole simulation) ----------

    private final String id;
    private final int arrivalTime;
    private final int burstTime;
    private final int priority;

    // ---------- Simulation state (filled in by the scheduler) ----------

    /** CPU time still needed. Starts equal to {@link #burstTime}. */
    private int remainingTime;

    /** Time when the process first got the CPU, or -1 if it has not run yet. */
    private int firstRunTime = -1;

    /** Time when the process finished, or -1 if it has not finished yet. */
    private int finishTime = -1;

    /** Current ACS tier: 1 or 2. Every process starts in Tier 1. */
    private int tier = 1;

    /** Time when the process was demoted to Tier 2, or -1 if it is not in Tier 2. */
    private int tier2EntryTime = -1;

    /**
     * Creates a new process.
     *
     * @param id          short name such as {@code "P1"}
     * @param arrivalTime time when the process arrives
     * @param burstTime   total CPU time the process needs
     * @param priority    base priority (lower number = higher priority)
     */
    public Process(String id, int arrivalTime, int burstTime, int priority) {
        this.id = Objects.requireNonNull(id, "id");
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }

    /**
     * Returns a fresh process with the same input data and clean simulation state.
     *
     * <p>Schedulers must work on copies so the input list is never modified.</p>
     *
     * @return a new {@code Process} with the same id, arrival, burst and priority
     */
    public Process copy() {
        return new Process(id, arrivalTime, burstTime, priority);
    }

    // ---------- Input getters ----------

    public String getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    // ---------- Simulation state getters / setters ----------

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getFirstRunTime() {
        return firstRunTime;
    }

    public void setFirstRunTime(int firstRunTime) {
        this.firstRunTime = firstRunTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getTier2EntryTime() {
        return tier2EntryTime;
    }

    public void setTier2EntryTime(int tier2EntryTime) {
        this.tier2EntryTime = tier2EntryTime;
    }

    // ---------- Derived values ----------

    /** @return {@code true} when the process has no CPU time left */
    public boolean isFinished() {
        return remainingTime <= 0;
    }

    /**
     * Turnaround time = finish time - arrival time.
     *
     * @return the turnaround time, or -1 if the process has not finished yet
     */
    public int getTurnaroundTime() {
        if (finishTime < 0) {
            return -1;
        }
        return finishTime - arrivalTime;
    }

    /**
     * Waiting time = turnaround time - burst time.
     *
     * <p>This includes context-switch time because the context switches are part
     * of the turnaround time.</p>
     *
     * @return the waiting time, or -1 if the process has not finished yet
     */
    public int getWaitingTime() {
        int turnaround = getTurnaroundTime();
        if (turnaround < 0) {
            return -1;
        }
        return turnaround - burstTime;
    }

    /**
     * Response time = first run time - arrival time.
     *
     * @return the response time, or -1 if the process has not run yet
     */
    public int getResponseTime() {
        if (firstRunTime < 0) {
            return -1;
        }
        return firstRunTime - arrivalTime;
    }

    /** Returns just the id, which keeps debug output short. */
    @Override
    public String toString() {
        return id;
    }
}
