package scheduler;

import java.util.List;
import java.util.Objects;

public record SimulationResult(
        String schedulerName,
        List<Process> processes,
        List<Snapshot> snapshots) {

    public SimulationResult {
        Objects.requireNonNull(schedulerName, "schedulerName");
        processes = List.copyOf(processes);
        snapshots = List.copyOf(snapshots);
    }
}
