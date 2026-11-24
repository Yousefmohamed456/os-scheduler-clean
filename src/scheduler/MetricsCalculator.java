package scheduler;

import java.util.List;
import java.util.Locale;

public final class MetricsCalculator {

    public static double averageWaiting(SimulationResult result) {
        List<Process> processes = result.processes();
        if (processes.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (Process p : processes) {
            total += p.getWaitingTime();
        }
        return (double) total / processes.size();
    }

    public static double averageTurnaround(SimulationResult result) {
        List<Process> processes = result.processes();
        if (processes.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (Process p : processes) {
            total += p.getTurnaroundTime();
        }
        return (double) total / processes.size();
    }

    public static void printTable(SimulationResult result) {
        System.out.println("Process | Arrival Time | Finish Time | Turnaround Time | Waiting Time | Response Time");
        for (Process p : result.processes()) {
            System.out.printf("%-7s | %12d | %11d | %15d | %12d | %13d%n",
                    p.getId(),
                    p.getArrivalTime(),
                    p.getFinishTime(),
                    p.getTurnaroundTime(),
                    p.getWaitingTime(),
                    p.getResponseTime());
        }
        System.out.println("Average Turnaround Time: " + averageLine(result, true) + " time units");
        System.out.println("Average Waiting Time: " + averageLine(result, false) + " time units");
    }

    private static String averageLine(SimulationResult result, boolean turnaround) {
        List<Process> processes = result.processes();
        if (processes.isEmpty()) {
            return "(0)/0 = 0.00";
        }
        int total = 0;
        StringBuilder numerator = new StringBuilder("(");
        for (int i = 0; i < processes.size(); i++) {
            int value = turnaround
                    ? processes.get(i).getTurnaroundTime()
                    : processes.get(i).getWaitingTime();
            total += value;
            if (i > 0) {
                numerator.append(" + ");
            }
            numerator.append(value);
        }
        numerator.append(")/").append(processes.size()).append(" = ");
        numerator.append(String.format(Locale.US, "%.2f", (double) total / processes.size()));
        return numerator.toString();
    }
}
