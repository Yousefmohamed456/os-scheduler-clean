package scheduler;

import java.util.Objects;

public class Process {

    private final String id;
    private final int arrivalTime;
    private final int burstTime;
    private final int priority;

    private int remainingTime;
    private int firstRunTime = -1;
    private int finishTime = -1;
    private int tier = 1;
    private int tier2EntryTime = -1;

    public Process(String id, int arrivalTime, int burstTime, int priority) {
        this.id = Objects.requireNonNull(id, "id");
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }

    public Process copy() {
        return new Process(id, arrivalTime, burstTime, priority);
    }

    public String getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getFirstRunTime() {
        return firstRunTime;
    }

    public void setFirstRunTime(int firstRunTime) {
        this.firstRunTime = firstRunTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getTier2EntryTime() {
        return tier2EntryTime;
    }

    public void setTier2EntryTime(int tier2EntryTime) {
        this.tier2EntryTime = tier2EntryTime;
    }

    public boolean isFinished() {
        return remainingTime <= 0;
    }

    public int getTurnaroundTime() {
        if (finishTime < 0) {
            return -1;
        }
        return finishTime - arrivalTime;
    }

    public int getWaitingTime() {
        int turnaround = getTurnaroundTime();
        if (turnaround < 0) {
            return -1;
        }
        return turnaround - burstTime;
    }

    public int getResponseTime() {
        if (firstRunTime < 0) {
            return -1;
        }
        return firstRunTime - arrivalTime;
    }

    @Override
    public String toString() {
        return id;
    }
}
