import scheduler.AcsScheduler;
import scheduler.MetricsCalculator;
import scheduler.Process;
import scheduler.SimulationResult;
import scheduler.SjfScheduler;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExperimentRunner {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int LEFT = 95;
    private static final int RIGHT = 45;
    private static final int TOP = 70;
    private static final int BOTTOM = 85;
    private static final int[] CS_VALUES = {0, 1, 2, 3};
    private static final int[] Q_VALUES = {1, 2, 3, 4, 5};
    private static final Color[] COLORS = {
            new Color(31, 119, 180),
            new Color(214, 39, 40),
            new Color(44, 160, 44),
            new Color(255, 127, 14)
    };

    public static void main(String[] args) throws IOException {
        List<TestCase> cases = readCases(Path.of("test-cases", "testcases.txt"));
        Files.createDirectories(Path.of("report", "graphs"));

        StringBuilder csv = new StringBuilder("test_case,scheduler,cs,q,avg_waiting,avg_turnaround\n");

        for (int index = 0; index < cases.size(); index++) {
            TestCase testCase = cases.get(index);
            String caseId = "case" + (index + 1);

            double[][] acsWaiting = new double[CS_VALUES.length][Q_VALUES.length];
            double[][] acsTurnaround = new double[CS_VALUES.length][Q_VALUES.length];

            for (int c = 0; c < CS_VALUES.length; c++) {
                for (int q = 0; q < Q_VALUES.length; q++) {
                    SimulationResult result = new AcsScheduler().run(
                            copy(testCase.processes), Q_VALUES[q], CS_VALUES[c]);
                    acsWaiting[c][q] = MetricsCalculator.averageWaiting(result);
                    acsTurnaround[c][q] = MetricsCalculator.averageTurnaround(result);
                    csv.append(caseId).append(",ACS,")
                            .append(CS_VALUES[c]).append(',')
                            .append(Q_VALUES[q]).append(',')
                            .append(format(acsWaiting[c][q])).append(',')
                            .append(format(acsTurnaround[c][q])).append('\n');
                }
            }

            double[] sjfWaiting = new double[CS_VALUES.length];
            double[] sjfTurnaround = new double[CS_VALUES.length];
            for (int c = 0; c < CS_VALUES.length; c++) {
                SimulationResult result = new SjfScheduler().run(
                        copy(testCase.processes), testCase.quantum, CS_VALUES[c]);
                sjfWaiting[c] = MetricsCalculator.averageWaiting(result);
                sjfTurnaround[c] = MetricsCalculator.averageTurnaround(result);
                csv.append(caseId).append(",SJF,")
                        .append(CS_VALUES[c]).append(",na,")
                        .append(format(sjfWaiting[c])).append(',')
                        .append(format(sjfTurnaround[c])).append('\n');
            }

            writeChart(
                    Path.of("report", "graphs", caseId + "_acs_waiting_q.png"),
                    testCase.name + " ACS: average waiting time vs quantum",
                    "Quantum (time units)",
                    "Average waiting time (time units)",
                    Q_VALUES,
                    CS_VALUES,
                    acsWaiting,
                    true);

            writeChart(
                    Path.of("report", "graphs", caseId + "_acs_turnaround_q.png"),
                    testCase.name + " ACS: average turnaround time vs quantum",
                    "Quantum (time units)",
                    "Average turnaround time (time units)",
                    Q_VALUES,
                    CS_VALUES,
                    acsTurnaround,
                    true);

            writeChart(
                    Path.of("report", "graphs", caseId + "_sjf_waiting_cs.png"),
                    testCase.name + " SJF: average waiting time vs context switch",
                    "Context switch time (time units)",
                    "Average waiting time (time units)",
                    CS_VALUES,
                    new int[]{0},
                    new double[][]{sjfWaiting},
                    false);

            writeChart(
                    Path.of("report", "graphs", caseId + "_sjf_turnaround_cs.png"),
                    testCase.name + " SJF: average turnaround time vs context switch",
                    "Context switch time (time units)",
                    "Average turnaround time (time units)",
                    CS_VALUES,
                    new int[]{0},
                    new double[][]{sjfTurnaround},
                    false);
        }

        Files.writeString(Path.of("report", "experiment-data.csv"), csv.toString());
        System.out.println("Generated graphs and report/experiment-data.csv");
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

    private static List<Process> copy(List<Process> input) {
        List<Process> result = new ArrayList<>();
        for (Process p : input) {
            result.add(p.copy());
        }
        return result;
    }

    private static void writeChart(Path path, String title, String xLabel, String yLabel,
                                   int[] xValues, int[] seriesLabels, double[][] series,
                                   boolean showLegend) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.BLACK);

        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        drawCentered(g, title, WIDTH / 2, 38);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));

        double maxY = 0.0;
        for (double[] values : series) {
            for (double value : values) {
                maxY = Math.max(maxY, value);
            }
        }
        maxY = Math.max(1.0, Math.ceil(maxY * 1.15));

        int plotWidth = WIDTH - LEFT - RIGHT;
        int plotHeight = HEIGHT - TOP - BOTTOM;

        g.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 5; i++) {
            int y = TOP + plotHeight - (int) Math.round(plotHeight * i / 5.0);
            g.drawLine(LEFT, y, LEFT + plotWidth, y);
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(LEFT, TOP, LEFT, TOP + plotHeight);
        g.drawLine(LEFT, TOP + plotHeight, LEFT + plotWidth, TOP + plotHeight);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        for (int i = 0; i <= 5; i++) {
            double value = maxY * i / 5.0;
            int y = TOP + plotHeight - (int) Math.round(plotHeight * i / 5.0);
            String label = String.format("%.1f", value);
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(label, LEFT - metrics.stringWidth(label) - 10, y + 5);
        }

        for (int i = 0; i < xValues.length; i++) {
            int x = xPosition(i, xValues.length, plotWidth);
            String label = Integer.toString(xValues[i]);
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(label, x - metrics.stringWidth(label) / 2, TOP + plotHeight + 24);
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        drawCentered(g, xLabel, LEFT + plotWidth / 2, HEIGHT - 30);
        drawRotated(g, yLabel, 26, TOP + plotHeight / 2);

        for (int s = 0; s < series.length; s++) {
            g.setColor(COLORS[s % COLORS.length]);
            g.setStroke(new BasicStroke(2.5f));
            int previousX = 0;
            int previousY = 0;
            for (int i = 0; i < xValues.length; i++) {
                int x = xPosition(i, xValues.length, plotWidth);
                int y = TOP + plotHeight - (int) Math.round(series[s][i] / maxY * plotHeight);
                if (i > 0) {
                    g.drawLine(previousX, previousY, x, y);
                }
                g.fillOval(x - 4, y - 4, 8, 8);
                previousX = x;
                previousY = y;
            }
        }

        if (showLegend) {
            int legendX = LEFT + plotWidth - 150;
            int legendY = TOP + 15;
            g.setColor(new Color(255, 255, 255, 220));
            g.fillRect(legendX - 10, legendY - 10, 150, series.length * 24 + 10);
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            for (int s = 0; s < series.length; s++) {
                int y = legendY + s * 24;
                g.setColor(COLORS[s % COLORS.length]);
                g.setStroke(new BasicStroke(2.5f));
                g.drawLine(legendX, y, legendX + 25, y);
                g.fillOval(legendX + 10, y - 3, 6, 6);
                g.setColor(Color.BLACK);
                g.drawString("CS = " + seriesLabels[s], legendX + 35, y + 5);
            }
        }

        g.dispose();
        ImageIO.write(image, "png", path.toFile());
    }

    private static int xPosition(int index, int count, int plotWidth) {
        if (count == 1) {
            return LEFT + plotWidth / 2;
        }
        return LEFT + (int) Math.round(plotWidth * index / (double) (count - 1));
    }

    private static void drawCentered(Graphics2D g, String text, int centerX, int y) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, centerX - metrics.stringWidth(text) / 2, y);
    }

    private static void drawRotated(Graphics2D g, String text, int x, int centerY) {
        Graphics2D copy = (Graphics2D) g.create();
        copy.translate(x, centerY);
        copy.rotate(-Math.PI / 2);
        FontMetrics metrics = copy.getFontMetrics();
        copy.drawString(text, -metrics.stringWidth(text) / 2, 0);
        copy.dispose();
    }

    private static String format(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
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
