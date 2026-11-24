package scheduler;

import java.util.ArrayList;
import java.util.List;

public class Tier2Queue {

    private final List<Process> waiting = new ArrayList<>();

    public void add(Process p, int currentTime) {
        p.setTier(2);
        p.setTier2EntryTime(currentTime);
        waiting.add(p);
    }

    public Process pollNext(int currentTime) {
        if (waiting.isEmpty()) {
            return null;
        }
        Process best = waiting.get(0);
        for (Process p : waiting) {
            if (comesBefore(p, best, currentTime)) {
                best = p;
            }
        }
        waiting.remove(best);
        return best;
    }

    public boolean isEmpty() {
        return waiting.isEmpty();
    }

    public List<Process> getWaiting() {
        return new ArrayList<>(waiting);
    }

    private static boolean comesBefore(Process a, Process b, int currentTime) {
        int priorityA = AgingPolicy.getEffectivePriority(a, currentTime);
        int priorityB = AgingPolicy.getEffectivePriority(b, currentTime);
        if (priorityA != priorityB) {
            return priorityA < priorityB;
        }
        if (a.getTier2EntryTime() != b.getTier2EntryTime()) {
            return a.getTier2EntryTime() < b.getTier2EntryTime();
        }
        return a.getId().compareTo(b.getId()) < 0;
    }
}
