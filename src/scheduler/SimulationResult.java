package scheduler;

import java.util.List;
import java.util.Objects;

public record SimulationResult(
        String schedulerName,
        List<Process> processes,
        List<Snapshot> snapshots,
        List<TimeSlice> timeline) {

    public SimulationResult {
        Objects.requireNonNull(schedulerName, "schedulerName");
        processes = List.copyOf(processes);
        snapshots = List.copyOf(snapshots);
        timeline = List.copyOf(timeline);
    }

    public SimulationResult(String schedulerName, List<Process> processes, List<Snapshot> snapshots) {
        this(schedulerName, processes, snapshots, List.of());
    }

    public record TimeSlice(int time, String processId, boolean contextSwitch) {
    }
}
