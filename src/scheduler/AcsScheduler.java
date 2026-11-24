package scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcsScheduler implements Scheduler {

    @Override
    public String getName() {
        return "Adaptive Container Scheduler";
    }

    @Override
    public SimulationResult run(List<Process> input, int quantum, int contextSwitch) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be positive");
        }

        List<Process> processes = new ArrayList<>();
        for (Process p : input) {
            processes.add(p.copy());
        }

        Deque<Process> tier1 = new ArrayDeque<>();
        Tier2Queue tier2 = new Tier2Queue();
        Map<Integer, Snapshot> events = new HashMap<>();
        boolean[] added = new boolean[processes.size()];

        int time = 0;
        int finished = 0;
        Process running = null;
        boolean runningInTier2 = false;
        int quantumUsed = 0;
        Process lastProcess = null;
        boolean lastFinished = false;
        int contextSwitchRemaining = 0;
        Process contextSwitchNext = null;
        boolean contextSwitchNextFromTier2 = false;

        while (finished < processes.size()) {
            if (addArrivals(processes, added, tier1, time)) {
                events.put(time, makeSnapshot(time, null, tier1, tier2, ""));
            }

            if (contextSwitchRemaining > 0) {
                contextSwitchRemaining--;
                time++;
                if (addArrivals(processes, added, tier1, time)) {
                    events.put(time, makeSnapshot(time, null, tier1, tier2, ""));
                }
                if (contextSwitchRemaining == 0) {
                    running = contextSwitchNext;
                    runningInTier2 = contextSwitchNextFromTier2;
                    quantumUsed = 0;
                    if (running.getFirstRunTime() < 0) {
                        running.setFirstRunTime(time);
                    }
                    events.put(time, makeSnapshot(time, running.getId(), tier1, tier2, ""));
                }
                continue;
            }

            if (running == null) {
                boolean fromTier2 = tier1.isEmpty();
                Process next = fromTier2 ? bestTier2(tier2, time) : tier1.peekFirst();

                if (next == null) {
                    int nextArrival = nextArrivalTime(processes, added, time);
                    if (nextArrival == Integer.MAX_VALUE) {
                        break;
                    }
                    time = nextArrival;
                    lastProcess = null;
                    lastFinished = false;
                    continue;
                }

                boolean needsContextSwitch = lastProcess != null
                        && next != lastProcess
                        && contextSwitch > 0;

                if (needsContextSwitch) {
                    String event = contextSwitchText(lastProcess, next, lastFinished);
                    if (fromTier2) {
                        event = agingText(tier2.getWaiting(), time) + event;
                    }
                    events.put(time, makeSnapshot(time, null, tier1, tier2, event));
                    removeNext(tier1, tier2, next, fromTier2, time);
                    contextSwitchNext = next;
                    contextSwitchNextFromTier2 = fromTier2;
                    contextSwitchRemaining = contextSwitch;
                    lastProcess = null;
                    continue;
                }

                String event = fromTier2 ? agingText(tier2.getWaiting(), time) : "";
                events.put(time, makeSnapshot(time, next.getId(), tier1, tier2, event));
                removeNext(tier1, tier2, next, fromTier2, time);
                running = next;
                runningInTier2 = fromTier2;
                quantumUsed = 0;
                if (running.getFirstRunTime() < 0) {
                    running.setFirstRunTime(time);
                }
            }

            running.setRemainingTime(running.getRemainingTime() - 1);
            time++;
            if (addArrivals(processes, added, tier1, time)) {
                events.put(time, makeSnapshot(time, null, tier1, tier2, ""));
            }

            if (running.isFinished()) {
                running.setFinishTime(time);
                finished++;
                lastProcess = running;
                lastFinished = true;
                String completed = running.getId() + " completed";
                running = null;
                if (finished == processes.size()) {
                    completed = "All processes completed";
                }
                events.put(time, makeSnapshot(time, null, tier1, tier2, completed));
            } else if (!runningInTier2) {
                quantumUsed++;
                if (quantumUsed == quantum) {
                    tier2.add(running, time);
                    lastProcess = running;
                    lastFinished = false;
                    running = null;
                }
            }
        }

        List<Snapshot> snapshots = new ArrayList<>();
        for (int t = 0; t <= time; t++) {
            Snapshot snapshot = events.get(t);
            if (snapshot == null) {
                snapshot = new Snapshot(t, null, List.of(), List.of(), "");
            }
            snapshots.add(snapshot);
        }
        return new SimulationResult(getName(), processes, snapshots);
    }

    private static boolean addArrivals(List<Process> processes, boolean[] added,
                                       Deque<Process> tier1, int time) {
        boolean any = false;
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
            if (!added[i] && p.getArrivalTime() <= time) {
                tier1.addLast(p);
                added[i] = true;
                any = true;
            }
        }
        return any;
    }

    private static int nextArrivalTime(List<Process> processes, boolean[] added, int time) {
        int next = Integer.MAX_VALUE;
        for (int i = 0; i < processes.size(); i++) {
            if (!added[i]) {
                next = Math.min(next, processes.get(i).getArrivalTime());
            }
        }
        return next;
    }

    private static Process bestTier2(Tier2Queue tier2, int time) {
        Process best = null;
        for (Process p : tier2.getWaiting()) {
            if (best == null || comesBefore(p, best, time)) {
                best = p;
            }
        }
        return best;
    }

    private static boolean comesBefore(Process a, Process b, int time) {
        int priorityA = AgingPolicy.getEffectivePriority(a, time);
        int priorityB = AgingPolicy.getEffectivePriority(b, time);
        if (priorityA != priorityB) {
            return priorityA < priorityB;
        }
        if (a.getTier2EntryTime() != b.getTier2EntryTime()) {
            return a.getTier2EntryTime() < b.getTier2EntryTime();
        }
        return a.getId().compareTo(b.getId()) < 0;
    }

    private static void removeNext(Deque<Process> tier1, Tier2Queue tier2,
                                   Process next, boolean fromTier2, int time) {
        if (fromTier2) {
            tier2.pollNext(time);
        } else {
            tier1.remove(next);
        }
    }

    private static String contextSwitchText(Process previous, Process next, boolean previousFinished) {
        if (previousFinished) {
            return "Context switch loads " + next.getId();
        }
        return "Context switch saves " + previous.getId() + " and loads " + next.getId();
    }

    private static String agingText(List<Process> waiting, int time) {
        StringBuilder text = new StringBuilder();
        for (Process p : waiting) {
            int waitTime = time - p.getTier2EntryTime();
            int aging = waitTime / AgingPolicy.AGING_INTERVAL;
            int effective = p.getPriority() - aging;
            text.append("Aging: ")
                    .append(p.getId())
                    .append(": Waited from T=")
                    .append(p.getTier2EntryTime())
                    .append(" to T=")
                    .append(time)
                    .append(" (")
                    .append(waitTime)
                    .append(" units). Aging: floor(")
                    .append(waitTime)
                    .append("/4) = ")
                    .append(aging)
                    .append(". Effective Priority = ")
                    .append(p.getPriority())
                    .append(" - ")
                    .append(aging)
                    .append(" = ")
                    .append(effective)
                    .append('\n');
        }
        return text.toString();
    }

    private static Snapshot makeSnapshot(int time, String runningId,
                                         Deque<Process> tier1, Tier2Queue tier2,
                                         String event) {
        List<Snapshot.ProcessState> tier1States = new ArrayList<>();
        for (Process p : tier1) {
            tier1States.add(new Snapshot.ProcessState(p.getId(), p.getRemainingTime(), p.getPriority()));
        }

        List<Snapshot.ProcessState> tier2States = new ArrayList<>();
        for (Process p : tier2.getWaiting()) {
            tier2States.add(new Snapshot.ProcessState(
                    p.getId(),
                    p.getRemainingTime(),
                    AgingPolicy.getEffectivePriority(p, time)));
        }

        return new Snapshot(time, runningId, tier1States, tier2States, event);
    }
}
