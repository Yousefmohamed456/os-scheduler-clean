package scheduler;

import java.util.List;

public interface Scheduler {

    String getName();

    SimulationResult run(List<Process> input, int quantum, int contextSwitch);
}
