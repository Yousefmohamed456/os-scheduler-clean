package scheduler;

public final class AgingPolicy {

    public static final int AGING_INTERVAL = 4;

    public static int getEffectivePriority(Process p, int currentTime) {
        if (p.getTier2EntryTime() < 0) {
            return p.getPriority();
        }
        int waitTime = currentTime - p.getTier2EntryTime();
        if (waitTime < 0) {
            waitTime = 0;
        }
        return p.getPriority() - waitTime / AGING_INTERVAL;
    }
}
