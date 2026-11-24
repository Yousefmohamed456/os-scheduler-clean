package scheduler;

import java.util.ArrayList;
import java.util.List;

public class SjfScheduler implements Scheduler {

    @Override
    public String getName() {
        return "Preemptive SJF";
    }

    @Override
    public SimulationResult run(List<Process> input, int quantum, int contextSwitch) {
        for (Process p : input) {
            if (p.getBurstTime() <= 0) {
                throw new IllegalArgumentException("Burst time must be positive: " + p.getId());
            }
        }

        List<Process> processes = new ArrayList<>();
        for (Process p : input) {
            processes.add(p.copy());
        }

        int time = 0;
        int finished = 0;
        Process current = null;
        Process lastRan = null;
        boolean firstDispatch = true;

        while (finished < processes.size()) {
            if (current == null) {
                Process next = chooseNext(processes, time);

                if (next == null) {
                    time = nextArrivalTime(processes, time);
                    lastRan = null;
                    firstDispatch = true;
                    continue;
                }

                if (!firstDispatch && lastRan != null && next != lastRan) {
                    time += contextSwitch;
                    next = chooseNext(processes, time);
                }

                current = next;
                lastRan = null;
                firstDispatch = false;

                if (current.getFirstRunTime() < 0) {
                    current.setFirstRunTime(time);
                }
            }

            current.setRemainingTime(current.getRemainingTime() - 1);
            time++;

            if (current.isFinished()) {
                current.setFinishTime(time);
                finished++;
                lastRan = current;
                current = null;
            } else {
                Process best = chooseNext(processes, time);
                if (best != null && best != current && isBetter(best, current)) {
                    lastRan = current;
                    current = null;
                }
            }
        }

        return new SimulationResult(getName(), processes, List.of());
    }

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

    private static boolean isBetter(Process a, Process b) {
        if (a.getRemainingTime() != b.getRemainingTime()) {
            return a.getRemainingTime() < b.getRemainingTime();
        }
        if (a.getArrivalTime() != b.getArrivalTime()) {
            return a.getArrivalTime() < b.getArrivalTime();
        }
        return a.getId().compareTo(b.getId()) < 0;
    }

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
