package scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Preemptive Shortest Job First (Shortest Remaining Time First).
 *
 * <p>Owner: Member 1.</p>
 *
 * <p>At every time unit, the arrived and unfinished process with the smallest
 * remaining time runs. Ties are broken by earlier arrival time, then by id.</p>
 *
 * <p>Context switching: when the CPU changes from one process to a different
 * process, {@code contextSwitch} time units pass with no process progress. The
 * cost is also paid after a process finishes. There is no switch at the very
 * start, no switch when the same process keeps running, and no switch when the
 * CPU was idle. The quantum is ignored.</p>
 */
public class SjfScheduler implements Scheduler {

    @Override
    public String getName() {
        return "Preemptive SJF";
    }

    @Override
    public SimulationResult run(List<Process> input, int quantum, int contextSwitch) {
        // The test cases use positive burst times. Reject bad input instead of
        // looping forever on a process that can never finish.
        for (Process p : input) {
            if (p.getBurstTime() <= 0) {
                throw new IllegalArgumentException("Burst time must be positive: " + p.getId());
            }
        }

        // Work on copies so the caller's list and processes are never modified.
        List<Process> processes = new ArrayList<>();
        for (Process p : input) {
            processes.add(p.copy());
        }

        int time = 0;
        int finished = 0;
        Process current = null;
        Process lastRan = null;
        boolean firstDispatch = true;

        // Main loop: run one time unit per iteration. Context switches and idle
        // gaps move time forward without running a process.
        while (finished < processes.size()) {
            if (current == null) {
                Process next = chooseNext(processes, time);

                if (next == null) {
                    // Nobody has arrived yet. Jump to the next arrival; the first
                    // process after an idle gap starts without a context switch.
                    time = nextArrivalTime(processes, time);
                    lastRan = null;
                    firstDispatch = true;
                    continue;
                }

                if (!firstDispatch && lastRan != null && next != lastRan) {
                    // Switch to a different process: spend the context-switch
                    // time, then choose again using arrivals that came by then.
                    time += contextSwitch;
                    next = chooseNext(processes, time);
                }

                current = next;
                lastRan = null;
                firstDispatch = false;

                // First real CPU time, not the context-switch time.
                if (current.getFirstRunTime() < 0) {
                    current.setFirstRunTime(time);
                }
            }

            // Run the current process for one time unit.
            current.setRemainingTime(current.getRemainingTime() - 1);
            time++;

            if (current.isFinished()) {
                current.setFinishTime(time);
                finished++;
                lastRan = current;
                current = null;
            } else {
                // Check whether a newly arrived process should take over.
                Process best = chooseNext(processes, time);
                if (best != null && best != current && isBetter(best, current)) {
                    lastRan = current;
                    current = null;
                }
            }
        }

        // The work list is already in the original input order.
        return new SimulationResult(getName(), processes, List.of());
    }

    /**
     * Returns the best arrived, unfinished process at {@code time}, or null if
     * no process is ready.
     */
    private static Process chooseNext(List<Process> processes, int time) {
        Process best = null;

        for (Process p : processes) {
            if (p.getArrivalTime() <= time && !p.isFinished()) {
                if (best == null || isBetter(p, best)) {
                    best = p;
                }
            }
        }
        return best;
    }

    /** SRTF order: remaining time, then arrival time, then id. */
    private static boolean isBetter(Process a, Process b) {
        if (a.getRemainingTime() != b.getRemainingTime()) {
            return a.getRemainingTime() < b.getRemainingTime();
        }
        if (a.getArrivalTime() != b.getArrivalTime()) {
            return a.getArrivalTime() < b.getArrivalTime();
        }
        return a.getId().compareTo(b.getId()) < 0;
    }

    /** Earliest arrival time of an unfinished process after {@code time}. */
    private static int nextArrivalTime(List<Process> processes, int time) {
        int next = Integer.MAX_VALUE;

        for (Process p : processes) {
            if (!p.isFinished() && p.getArrivalTime() > time) {
                next = Math.min(next, p.getArrivalTime());
            }
        }
        return next;
    }
}
