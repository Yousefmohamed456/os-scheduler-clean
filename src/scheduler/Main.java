package scheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Main {

    public static void main(String[] args) throws IOException {
        List<TestCase> cases = readCases(Path.of("test-cases", "testcases.txt"));

        for (int i = 0; i < cases.size(); i++) {
            TestCase testCase = cases.get(i);
            printInput(testCase);
            runAcs(testCase);
            runSjf(testCase);
            if (i < cases.size() - 1) {
                System.out.println();
            }
        }
    }

    private static void printInput(TestCase testCase) {
        System.out.println(testCase.name);
        System.out.println("Process | Arrival Time | Burst Time | Initial Priority");
        for (Process p : testCase.processes) {
            System.out.printf("%-7s | %12d | %10d | %16d%n",
                    p.getId(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority());
        }
        System.out.println("Q = " + testCase.quantum);
        System.out.println("CS = " + testCase.contextSwitch);
        System.out.println();
    }

    private static void runAcs(TestCase testCase) {
        SimulationResult result = new AcsScheduler().run(
                testCase.processes, testCase.quantum, testCase.contextSwitch);
        System.out.println("ACS Tier Matrix");
        MatrixPrinter.print(result.snapshots());
        System.out.println();
        System.out.println("ACS Analysis");
        MetricsCalculator.printTable(result);
        System.out.println();
    }

    private static void runSjf(TestCase testCase) {
        SimulationResult result = new SjfScheduler().run(
                testCase.processes, testCase.quantum, testCase.contextSwitch);
        System.out.println("SJF Analysis");
        MetricsCalculator.printTable(result);
        System.out.println();
    }

    private static List<TestCase> readCases(Path path) throws IOException {
        List<TestCase> cases = new ArrayList<>();
        String name = null;
        int quantum = 0;
        int contextSwitch = 0;
        List<Process> processes = new ArrayList<>();

        for (String raw : Files.readAllLines(path)) {
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|");
            switch (parts[0]) {
                case "CASE" -> {
                    if (name != null) {
                        cases.add(new TestCase(name, quantum, contextSwitch, processes));
                    }
                    name = parts[1];
                    quantum = 0;
                    contextSwitch = 0;
                    processes = new ArrayList<>();
                }
                case "Q" -> quantum = Integer.parseInt(parts[1]);
                case "CS" -> contextSwitch = Integer.parseInt(parts[1]);
                case "PROCESS" -> processes.add(new Process(
                        parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4])));
                default -> {
                }
            }
        }

        if (name != null) {
            cases.add(new TestCase(name, quantum, contextSwitch, processes));
        }
        return cases;
    }

    private static final class TestCase {
        private final String name;
        private final int quantum;
        private final int contextSwitch;
        private final List<Process> processes;

        private TestCase(String name, int quantum, int contextSwitch, List<Process> processes) {
            this.name = name;
            this.quantum = quantum;
            this.contextSwitch = contextSwitch;
            this.processes = processes;
        }
    }
}
