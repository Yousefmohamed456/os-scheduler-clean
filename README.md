# CS241 - Assignment 2: Cloud-Cluster CPU Scheduling

A Java 17+ simulator for two CPU schedulers:

1. **Preemptive SJF** - the process with the shortest remaining time always runs.
2. **Adaptive Container Scheduler (ACS)** - a three-tier scheduler with a Round
   Robin Tier 1, a non-preemptive priority Tier 2, and aging in Tier 3.

The simulator prints a tier matrix for each time step and a metrics table with
Waiting, Turnaround and Response time per process, plus the two averages.

This repository contains the shared base, Preemptive SJF, the complete ACS, the
tier-matrix and metrics output, four test cases, the CS/Q experiments and the
PDF report. See [Status](#status) for the current state.

## Team

| Member | Name | Student ID | Responsibility |
|---|---|---|---|
| 1 | `<name>` | `<id>` | Shared base, `SjfScheduler`, GUI bonus |
| 2 | `<name>` | `<id>` | `AcsScheduler`: Tier 1 + Tier 2, CS, snapshots |
| 3 | `<name>` | `<id>` | `AgingPolicy`, `MatrixPrinter`, `MetricsCalculator` |
| 4 | `<name>` | `<id>` | Test cases, experiments, graphs, report, `Main` |

- **Course:** CS241 Operating Systems, Cairo University
- **Group:** `<group>`
- **TA:** `<TA name>`

## Requirements summary

1. **Simulator:** preemptive SJF and the three-tier ACS. Context switching (CS)
   must be counted in all preemptive algorithms.
   - **Tier 1:** all new processes start in a Round Robin queue with quantum `Q`.
   - **Tier 2:** a process that uses its entire quantum without finishing is
     demoted to a non-preemptive priority queue. Once it starts there, it cannot
     be preempted.
   - **Tier 3:** aging prevents starvation:
     `aging = floor(waitTime / 4)`, `effectivePriority = priority - aging`.
     Lower number = higher priority. `waitTime` starts when the process enters
     Tier 2.
2. **Output:** the tier matrix per time step, Waiting / Turnaround / Response
   time per process, and Average Waiting / Average Turnaround.
   - `Waiting = Turnaround - Burst` (includes CS time)
   - `Turnaround = Finish - Arrival`
   - `Response = FirstRun - Arrival`
3. **Report:** a PDF that runs different CS and Q values for each test case,
   graphs the effect of CS and Q on the averages, and discusses the ACS Tier 2
   demotion trade-offs (CPU utilization vs Response Time).
4. **Bonus:** a GUI for the simulator.

See [`docs/assignment-notes.md`](docs/assignment-notes.md) for the full summary,
the expected-result traces and the open questions.

## Repository structure

```text
.
├── README.md
├── .gitignore
├── docs/
│   ├── assignment-notes.md
│   ├── class-diagram.md
│   ├── git-workflow.md
│   └── team-plan.md
├── experiments/
│   └── ExperimentRunner.java
├── report/
│   ├── README.md
│   ├── experiment-data.csv
│   ├── report.html
│   ├── report.pdf
│   └── graphs/
│       └── 24 PNG files
├── src/
│   └── scheduler/
│       ├── AcsScheduler.java
│       ├── AgingPolicy.java
│       ├── Main.java
│       ├── MatrixPrinter.java
│       ├── MetricsCalculator.java
│       ├── Process.java
│       ├── Scheduler.java
│       ├── SchedulerGui.java
│       ├── SimulationResult.java
│       ├── SjfScheduler.java
│       ├── Snapshot.java
│       └── Tier2Queue.java
└── test-cases/
    ├── README.md
    └── testcases.txt
```

`out/` is created by the compiler and is ignored by Git. The expected test-case
results are documented in [`docs/assignment-notes.md`](docs/assignment-notes.md)
and printed by `Main`.

## Compile and run

**Requirement:** JDK 17 or newer, with `javac` and `java` on your `PATH`.

### Windows PowerShell

```powershell
javac -d out src/scheduler/*.java
java -cp out scheduler.Main
```

If PowerShell does not expand the wildcard on your machine, use:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem src\scheduler\*.java).FullName
java -cp out scheduler.Main
```

### Linux / macOS

```bash
mkdir -p out
javac -d out src/scheduler/*.java
java -cp out scheduler.Main
```

### Windows cmd.exe (optional)

```bat
if not exist out mkdir out
javac -d out src\scheduler\*.java
java -cp out scheduler.Main
```

`Main` reads `test-cases/testcases.txt`, runs the ACS and SJF on all four test
cases, and prints the ACS tier matrix and both analysis tables for each case.

### GUI (bonus)

```powershell
java -cp out scheduler.SchedulerGui
```

The window opens with Test Case 1 already filled in. Edit the process table,
set `Q` and `CS`, pick a scheduler and press **Run**. The **Gantt Chart** tab
draws the schedule with context-switch periods, the **Tier Matrix** tab shows
the ACS matrix in the console format, and the **Analysis** tab shows Waiting,
Turnaround and Response per process plus the two averages.

## Test cases

All four test cases are stored in
[`test-cases/testcases.txt`](test-cases/testcases.txt) and read by `Main`:

- **Test Case 1 (assignment):** `Q = 3`, `CS = 0`.
- **Test Case 2 (assignment):** `Q = 3`, `CS = 1`.
- **Test Case 3 (generated):** `Q = 2`, `CS = 1`.
- **Test Case 4 (generated):** `Q = 2`, `CS = 1`.

The expected results, the traces, the assignment typos and the open questions
are in [`docs/assignment-notes.md`](docs/assignment-notes.md). The full CS/Q
data is in [`report/experiment-data.csv`](report/experiment-data.csv), and the
analysis is in [`report/report.pdf`](report/report.pdf).

## Documentation

- [Assignment notes](docs/assignment-notes.md) - requirements, expected-result
  traces, known typo, open questions.
- [Class diagram](docs/class-diagram.md) - Mermaid diagram and class-by-class
  explanation.
- [Team plan](docs/team-plan.md) - ownership, who waits for whom, contracts and
  team decisions.
- [Git workflow](docs/git-workflow.md) - branches, commits, pull requests and
  conflict resolution.
- [Report template](report/README.md) - what the PDF report must contain.

## Git workflow summary

- Work is committed directly on `main` in this local repository.
- Small commits with clear messages.
- Recompile and rerun `Main` after every change.

The detailed version is in [`docs/git-workflow.md`](docs/git-workflow.md).

## Implementation ownership

| Member | Files |
|---|---|
| 1 | Shared base, `SjfScheduler` |
| 2 | `AcsScheduler`, `Tier2Queue` |
| 3 | `AgingPolicy`, `MatrixPrinter`, `MetricsCalculator` |
| 4 | `Main`, `test-cases/`, `experiments/`, `report/` |

The public class names and method signatures in `Process`, `Scheduler` and
`Snapshot` were kept unchanged because they are the shared contracts.
`SimulationResult` gained a fourth component, `timeline`, so the GUI can draw
the Gantt chart; its original three-argument constructor is still there, so
existing call sites did not change. `MatrixPrinter.print` now delegates to the
new `MatrixPrinter.format`, which returns the same text instead of printing it.

## Submission checklist

- [ ] ZIP named `A2_TAname_Group_ID1_ID2_ID3_ID4`.
- [ ] Source code only. Confirm with the TA whether the PDF report is submitted
      separately (see Open questions in `docs/assignment-notes.md`).
- [ ] No `out/`, `*.class`, `.idea/`, `.vscode/` or `*.zip` inside the ZIP.
- [ ] `javac -d out src/scheduler/*.java` works from a clean checkout.
- [ ] Test Case 1 matches the expected finish/turnaround/waiting values.
- [ ] Test Case 2 matches the expected finish/turnaround/waiting values.
- [ ] Each assignment rule is checked; each violation costs 1 mark.
- [ ] All members understand all code.

## Status

- [x] Shared base: `Process`, `Scheduler`, `SimulationResult`, `Snapshot` (Member 1)
- [x] Preemptive SJF implemented (1 mark)
- [x] ACS Tier 1 Round Robin implemented (1 mark)
- [x] ACS Tier 2 demotion + non-preemptive priority implemented (1 mark)
- [x] ACS Tier 3 aging implemented (1 mark)
- [x] Both assignment test cases pass (0.5 mark)
- [x] Analysis table per test case (0.5 mark)
- [x] PDF report with CS/Q graphs and Tier 2 trade-offs discussion (1 mark)
- [x] GUI (bonus, Member 1): `scheduler.SchedulerGui`
- [ ] Final ZIP checked and named correctly
