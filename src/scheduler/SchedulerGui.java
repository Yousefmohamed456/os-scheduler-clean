package scheduler;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SchedulerGui {

    private static final String[] PROCESS_COLUMNS = {
            "Process", "Arrival Time", "Burst Time", "Initial Priority"
    };
    private static final String[] ANALYSIS_COLUMNS = {
            "Process", "Arrival Time", "Finish Time", "Turnaround Time", "Waiting Time", "Response Time"
    };
    private static final String[] SCHEDULER_NAMES = {
            "Preemptive SJF", "Adaptive Container Scheduler (ACS)"
    };
    private static final Object[][] TEST_CASE_1 = {
            {"P1", 0, 4, 2},
            {"P2", 1, 7, 3},
            {"P3", 3, 2, 1}
    };
    private static final Color[] PALETTE = {
            new Color(31, 119, 180),
            new Color(214, 39, 40),
            new Color(44, 160, 44),
            new Color(148, 103, 189),
            new Color(255, 127, 14),
            new Color(140, 86, 75),
            new Color(227, 119, 194),
            new Color(127, 127, 127)
    };
    private static final Color SWITCH_COLOR = new Color(120, 120, 120);
    private static final Color IDLE_COLOR = new Color(228, 228, 228);

    private final DefaultTableModel processModel = new DefaultTableModel(PROCESS_COLUMNS, 0);
    private final JTable processTable = new JTable(processModel);
    private final JTextField quantumField = new JTextField("3", 4);
    private final JTextField contextSwitchField = new JTextField("0", 4);
    private final JComboBox<String> schedulerChoice = new JComboBox<>(SCHEDULER_NAMES);
    private final GanttPanel ganttPanel = new GanttPanel();
    private final JTextArea matrixArea = new JTextArea();
    private final DefaultTableModel analysisModel = new DefaultTableModel(ANALYSIS_COLUMNS, 0);
    private final JFrame frame = new JFrame("CS241 Assignment 2 - Scheduler GUI");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SchedulerGui());
    }

    private SchedulerGui() {
        addTestCaseOne();
        buildWindow();
    }

    private void buildWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));
        frame.add(buildInputPanel(), BorderLayout.NORTH);
        frame.add(buildResultTabs(), BorderLayout.CENTER);
        frame.setSize(1000, 720);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(buildControls(), BorderLayout.NORTH);
        panel.add(buildProcessPanel(), BorderLayout.CENTER);
        panel.add(buildRowButtons(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildControls() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Scheduler:"));
        panel.add(schedulerChoice);
        panel.add(new JLabel("Q:"));
        panel.add(quantumField);
        panel.add(new JLabel("CS:"));
        panel.add(contextSwitchField);
        JButton runButton = new JButton("Run");
        runButton.addActionListener(this::runSimulation);
        panel.add(runButton);
        return panel;
    }

    private JPanel buildProcessPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Processes"));
        panel.add(new JScrollPane(processTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRowButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Row");
        addButton.addActionListener(e -> addRow());
        JButton removeButton = new JButton("Remove Row");
        removeButton.addActionListener(e -> removeRow());
        panel.add(addButton);
        panel.add(removeButton);
        return panel;
    }

    private JTabbedPane buildResultTabs() {
        matrixArea.setEditable(false);
        matrixArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JTable analysisTable = new JTable(analysisModel);
        analysisTable.setDefaultEditor(Object.class, null);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Gantt Chart", ganttPanel);
        tabs.addTab("Tier Matrix", new JScrollPane(matrixArea));
        tabs.addTab("Analysis", new JScrollPane(analysisTable));
        return tabs;
    }

    private void addTestCaseOne() {
        for (Object[] row : TEST_CASE_1) {
            processModel.addRow(row);
        }
        schedulerChoice.setSelectedIndex(1);
    }

    private void addRow() {
        processModel.addRow(new Object[]{nextProcessId(), 0, 1, 1});
    }

    private String nextProcessId() {
        int number = processModel.getRowCount() + 1;
        while (findRow("P" + number) >= 0) {
            number++;
        }
        return "P" + number;
    }

    private int findRow(String id) {
        for (int row = 0; row < processModel.getRowCount(); row++) {
            if (id.equals(textAt(row, 0))) {
                return row;
            }
        }
        return -1;
    }

    private void removeRow() {
        int row = processTable.getSelectedRow();
        if (row < 0) {
            showMessage("Select a row first, then press Remove Row.");
            return;
        }
        processModel.removeRow(row);
    }

    private void runSimulation(ActionEvent event) {
        try {
            boolean acs = isAcs();
            List<Process> processes = readProcesses();
            int quantum = readField(quantumField, "Q", acs ? 1 : 0);
            int contextSwitch = readField(contextSwitchField, "CS", 0);
            SimulationResult result = acs
                    ? new AcsScheduler().run(processes, quantum, contextSwitch)
                    : new SjfScheduler().run(processes, quantum, contextSwitch);
            showResult(result, quantum, contextSwitch);
        } catch (RuntimeException error) {
            String message = error.getMessage();
            showMessage(message == null || message.isEmpty() ? error.toString() : message);
        }
    }

    private boolean isAcs() {
        return schedulerChoice.getSelectedIndex() == 1;
    }

    private List<Process> readProcesses() {
        if (processModel.getRowCount() == 0) {
            throw new IllegalArgumentException("Add at least one process row before running.");
        }
        List<Process> processes = new ArrayList<>();
        for (int row = 0; row < processModel.getRowCount(); row++) {
            processes.add(readProcess(row));
        }
        return processes;
    }

    private Process readProcess(int row) {
        String id = textAt(row, 0);
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Process ID in row " + (row + 1) + " must not be empty.");
        }
        int arrival = intAt(row, 1, "Arrival time", 0);
        int burst = intAt(row, 2, "Burst time", 1);
        int priority = intAt(row, 3, "Initial priority", Integer.MIN_VALUE);
        return new Process(id, arrival, burst, priority);
    }

    private int intAt(int row, int column, String label, int minimum) {
        String text = textAt(row, column);
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                    label + " in row " + (row + 1) + " must be a whole number.");
        }
        if (value < minimum) {
            throw new IllegalArgumentException(
                    label + " in row " + (row + 1) + " must be at least " + minimum + ".");
        }
        return value;
    }

    private String textAt(int row, int column) {
        Object value = processModel.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
    }

    private int readField(JTextField field, String label, int minimum) {
        String text = field.getText().trim();
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
        if (value < minimum) {
            throw new IllegalArgumentException(label + " must be at least " + minimum + ".");
        }
        return value;
    }

    private void showResult(SimulationResult result, int quantum, int contextSwitch) {
        ganttPanel.showResult(result, quantum, contextSwitch);
        matrixArea.setText(matrixText(result));
        matrixArea.setCaretPosition(0);
        fillAnalysisTable(result);
    }

    private String matrixText(SimulationResult result) {
        if (result.snapshots().isEmpty()) {
            return "The tier matrix only exists for the Adaptive Container Scheduler (ACS).\n"
                    + "Choose ACS and press Run to see it.";
        }
        return MatrixPrinter.format(result.snapshots());
    }

    private void fillAnalysisTable(SimulationResult result) {
        analysisModel.setRowCount(0);
        for (Process p : result.processes()) {
            analysisModel.addRow(new Object[]{
                    p.getId(),
                    p.getArrivalTime(),
                    p.getFinishTime(),
                    p.getTurnaroundTime(),
                    p.getWaitingTime(),
                    p.getResponseTime()
            });
        }
        analysisModel.addRow(new Object[]{
                "Average",
                "",
                "",
                format(MetricsCalculator.averageTurnaround(result)),
                format(MetricsCalculator.averageWaiting(result)),
                ""
        });
    }

    private static String format(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Scheduler GUI", JOptionPane.ERROR_MESSAGE);
    }

    private static final class GanttPanel extends JPanel {

        private List<Segment> segments = List.of();
        private Map<String, Color> colors = Map.of();
        private String title = "";

        void showResult(SimulationResult result, int quantum, int contextSwitch) {
            segments = buildSegments(result);
            colors = buildColors(result);
            title = result.schedulerName()
                    + " - Gantt chart (Q = " + quantum + ", CS = " + contextSwitch + ")";
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (segments.isEmpty()) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("Press Run to draw the schedule.", 24, 32);
                return;
            }
            g.setColor(Color.BLACK);
            g.drawString(title, 24, 24);
            int axisY = drawTimeline(g);
            drawLegend(g, axisY + 58);
        }

        private int drawTimeline(Graphics2D g) {
            int left = 30;
            int right = 20;
            int bandTop = 44;
            int bandHeight = 56;
            int chartWidth = Math.max(1, getWidth() - left - right);
            int elapsed = segments.get(segments.size() - 1).end();
            double unit = (double) chartWidth / elapsed;

            for (Segment segment : segments) {
                int x0 = left + (int) Math.round(segment.start() * unit);
                int x1 = left + (int) Math.round(segment.end() * unit);
                int width = Math.max(1, x1 - x0);
                g.setColor(colorFor(segment.label()));
                g.fillRect(x0, bandTop, width, bandHeight);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x0, bandTop, width, bandHeight);
                drawSegmentLabel(g, segment, x0, width, bandTop, bandHeight);
            }

            int axisY = bandTop + bandHeight + 4;
            g.setColor(Color.BLACK);
            g.drawLine(left, axisY, left + chartWidth, axisY);
            int step = Math.max(1, (elapsed + 19) / 20);
            for (int t = 0; t <= elapsed; t++) {
                int x = left + (int) Math.round(t * unit);
                g.drawLine(x, axisY, x, axisY + 4);
                if (t % step == 0) {
                    String text = String.valueOf(t);
                    int textWidth = g.getFontMetrics().stringWidth(text);
                    g.drawString(text, x - textWidth / 2, axisY + 17);
                }
            }
            String axisLabel = "Time (time units)";
            int labelWidth = g.getFontMetrics().stringWidth(axisLabel);
            g.drawString(axisLabel, left + (chartWidth - labelWidth) / 2, axisY + 36);
            return axisY;
        }

        private void drawSegmentLabel(Graphics2D g, Segment segment, int x0, int width,
                                      int bandTop, int bandHeight) {
            String text = segment.label();
            int textWidth = g.getFontMetrics().stringWidth(text);
            if (width < textWidth + 10) {
                return;
            }
            g.setColor(textColorFor(text));
            g.drawString(text, x0 + (width - textWidth) / 2, bandTop + bandHeight / 2 + 5);
        }

        private void drawLegend(Graphics2D g, int y) {
            int left = 30;
            int x = left;
            List<String> labels = new ArrayList<>(colors.keySet());
            labels.add("CS");
            labels.add("idle");
            for (String label : labels) {
                int width = g.getFontMetrics().stringWidth(label) + 34;
                if (x + width > getWidth() - 20) {
                    x = left;
                    y -= 22;
                }
                g.setColor(colorFor(label));
                g.fillRect(x, y - 11, 14, 14);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y - 11, 14, 14);
                g.setColor(Color.BLACK);
                g.drawString(label, x + 19, y);
                x += width;
            }
        }

        private Color colorFor(String label) {
            if ("CS".equals(label)) {
                return SWITCH_COLOR;
            }
            if ("idle".equals(label)) {
                return IDLE_COLOR;
            }
            Color color = colors.get(label);
            return color == null ? PALETTE[0] : color;
        }

        private Color textColorFor(String label) {
            if ("idle".equals(label)) {
                return new Color(60, 60, 60);
            }
            return Color.WHITE;
        }

        private static List<Segment> buildSegments(SimulationResult result) {
            int elapsed = 0;
            for (SimulationResult.TimeSlice slice : result.timeline()) {
                elapsed = Math.max(elapsed, slice.time() + 1);
            }
            for (Process p : result.processes()) {
                elapsed = Math.max(elapsed, p.getFinishTime());
            }
            if (elapsed <= 0) {
                return List.of();
            }
            String[] labels = new String[elapsed];
            for (SimulationResult.TimeSlice slice : result.timeline()) {
                if (slice.time() >= 0 && slice.time() < elapsed) {
                    labels[slice.time()] = slice.contextSwitch() ? "CS" : slice.processId();
                }
            }
            List<Segment> built = new ArrayList<>();
            int start = 0;
            String current = labelAt(labels, 0);
            for (int t = 1; t < elapsed; t++) {
                String next = labelAt(labels, t);
                if (!next.equals(current)) {
                    built.add(new Segment(start, t, current));
                    start = t;
                    current = next;
                }
            }
            built.add(new Segment(start, elapsed, current));
            return built;
        }

        private static Map<String, Color> buildColors(SimulationResult result) {
            Map<String, Color> built = new LinkedHashMap<>();
            int index = 0;
            for (Process p : result.processes()) {
                if (!built.containsKey(p.getId())) {
                    built.put(p.getId(), PALETTE[index % PALETTE.length]);
                    index++;
                }
            }
            return built;
        }

        private static String labelAt(String[] labels, int index) {
            String label = labels[index];
            return label == null ? "idle" : label;
        }

        private record Segment(int start, int end, String label) {
        }
    }
}
