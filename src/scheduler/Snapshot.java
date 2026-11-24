package scheduler;

import java.util.List;
import java.util.Objects;

public record Snapshot(
        int time,
        String runningId,
        List<ProcessState> tier1,
        List<ProcessState> tier2,
        String event) {

    public Snapshot {
        tier1 = List.copyOf(tier1);
        tier2 = List.copyOf(tier2);
        event = event == null ? "" : event;
    }

    public record ProcessState(String id, int remaining, int effectivePriority) {
        public ProcessState {
            Objects.requireNonNull(id, "id");
        }
    }
}
