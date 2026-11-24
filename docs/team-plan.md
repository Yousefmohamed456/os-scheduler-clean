# Team Plan

Replace `Member 1` ... `Member 4` with the real names before submitting.

## Ownership

| Member | Tasks | Marks |
|---|---|---|
| 1 | `Process` + interfaces (shared base), `SjfScheduler`, GUI bonus - **Done (base + SJF)** | SJF 1 + Bonus 1 |
| 2 | `AcsScheduler`: Tier 1 (RR) + Tier 2 (demotion, non-preemptive priority), CS handling, fills snapshots | Tier 1 + Tier 2 = 2 |
| 3 | `AgingPolicy` (Tier 3), `MatrixPrinter`, `MetricsCalculator` + analysis tables | Tier 3 1 + tables 0.5 |
| 4 | Test cases, experiments with different Q/CS, graphs, PDF report incl. Tier 2 trade-offs discussion, `Main` | Test cases 0.5 + Report 1 |

## Who waits for whom

- **Everyone** depends on `Process` + `Scheduler` + `Snapshot` +
  `SimulationResult` (Member 1). These are **already in the repo and
  implemented**, so nobody waits for the base anymore.
- **Member 1 (SJF):** **Done (base + SJF)** and on `main`. The GUI waits for all
  schedulers to finish.
- **Member 2:** needs only `Process` (already done). Implements `Tier2Queue` and
  calls the `AgingPolicy` contract; both are stubs in this clean repo.
- **Member 3:** depends on Member 2 only at the end (integration). Can work now
  with fake `Snapshot`s and fake finished `Process` objects.
- **Member 4:** test cases, report template and docs are independent.
  Experiments and graphs wait until SJF and ACS both work.
- **Integration order:** base -> SJF and ACS in parallel -> aging/matrix
  integration -> experiments -> report -> GUI -> final ZIP.

## Agreed contracts

Every member follows these four rules:

1. **Never mutate the input.** Do not change the input list or its `Process`
   objects. Start every scheduler with `Process.copy()`.
2. **Record the times.** Set `firstRunTime` the first time a process runs and
   `finishTime` when it finishes. Leave both at `-1` until then.
3. **ACS fills snapshots.** Record one `Snapshot` per time step with the running
   process, Tier 1 contents, Tier 2 contents and a short event. SJF may return
   an empty snapshot list because the tier matrix is an ACS feature.
4. **Demote through the queue.** On demotion, call
   `tier2Queue.add(process, currentTime)`. It sets `tier = 2` and
   `tier2EntryTime`, which keeps the aging rule consistent.

## Team decisions that can be changed

These are not fixed by the assignment. The team can change them if the TA
prefers a different rule, but everyone must use the same choice.

- **Tie-break:** Tier 2 uses effective priority, then earlier `tier2EntryTime`,
  then id. SJF uses shortest remaining time, then earlier arrival, then id.
- **No clamping:** `effectivePriority = priority - aging` is not clamped at 0.
  It may become negative after a long wait.
- **Context switch:** CS is counted only when the CPU switches to a different
  process. No CS before the first process and no CS after the last process.

## Working agreement

- One branch per member with remaining work; see
  [`git-workflow.md`](git-workflow.md).
- Small commits with clear messages.
- At least one teammate reviews every pull request.
- If a contract changes, update this file and tell the team before changing
  code that depends on it.
