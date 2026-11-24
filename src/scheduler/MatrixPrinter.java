package scheduler;

import java.util.ArrayList;
import java.util.List;

public final class MatrixPrinter {

    public static void print(List<Snapshot> snapshots) {
        System.out.print(format(snapshots));
    }

    public static String format(List<Snapshot> snapshots) {
        String separator = System.lineSeparator();
        StringBuilder output = new StringBuilder();
        output.append("Time | Process | Tier").append(separator);

        for (Snapshot snapshot : snapshots) {
            List<String> agingLines = new ArrayList<>();
            StringBuilder event = new StringBuilder();

            for (String line : snapshot.event().split("\n", -1)) {
                if (line.startsWith("Aging: ")) {
                    agingLines.add(line.substring(7));
                } else if (!line.isEmpty()) {
                    if (event.length() > 0) {
                        event.append(' ');
                    }
                    event.append(line);
                }
            }

            for (String line : agingLines) {
                output.append(line).append(separator);
            }

            String tier = tierText(snapshot.tier1(), snapshot.tier2());
            boolean showTier = !snapshot.tier1().isEmpty()
                    || !snapshot.tier2().isEmpty()
                    || event.length() > 0;

            if (!showTier) {
                output.append(snapshot.time()).append(" |  | ").append(separator);
                continue;
            }

            String process = snapshot.runningId() == null ? "" : snapshot.runningId();
            String eventText = event.length() == 0 ? "" : event + " ";
            output.append(snapshot.time())
                    .append(" | ")
                    .append(process)
                    .append(" | ")
                    .append(eventText)
                    .append(tier)
                    .append(separator);
        }
        return output.toString();
    }

    private static String tierText(List<Snapshot.ProcessState> tier1,
                                   List<Snapshot.ProcessState> tier2) {
        StringBuilder text = new StringBuilder();
        text.append("Tier 1: ").append(stateList(tier1));
        if (!tier2.isEmpty() || tier1.isEmpty()) {
            text.append(" Tier 2: ").append(stateList(tier2));
        }
        return text.toString();
    }

    private static String stateList(List<Snapshot.ProcessState> states) {
        if (states.isEmpty()) {
            return "[Empty]";
        }
        StringBuilder text = new StringBuilder("[");
        for (int i = 0; i < states.size(); i++) {
            if (i > 0) {
                text.append(", ");
            }
            Snapshot.ProcessState state = states.get(i);
            text.append(state.id())
                    .append(" (remaining: ")
                    .append(state.remaining())
                    .append(", Priority: ")
                    .append(state.effectivePriority())
                    .append(")");
        }
        return text.append("]").toString();
    }
}
