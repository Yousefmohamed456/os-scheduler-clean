# CS241 - Assignment 2: Cloud-Cluster CPU Scheduling

A Java 17+ simulator for two CPU schedulers:

1. **Preemptive SJF** - the process with the shortest remaining time always runs.
2. **Adaptive Container Scheduler (ACS)** - a three-tier scheduler with a Round
   Robin Tier 1, a non-preemptive priority Tier 2, and aging in Tier 3.

The simulator prints a tier matrix for each time step and a metrics table with
Waiting, Turnaround and Response time per process, plus the two averages.

This repository contains the shared base and Member 1's Preemptive SJF
implementation. The ACS scheduler and the remaining utilities are TODO stubs for
Members 2, 3 and 4 to implement. See [Status](#status) for the current state.

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
├── report/
│   ├── README.md
│   └── graphs/
│       └── .gitkeep
├── src/
│   └── scheduler/
│       ├── AcsScheduler.java
│       ├── AgingPolicy.java
│       ├── Main.java
│       ├── MatrixPrinter.java
│       ├── MetricsCalculator.java
│       ├── Process.java
│       ├── Scheduler.java
│       ├── SimulationResult.java
│       ├── SjfScheduler.java
│       ├── Snapshot.java
│       └── Tier2Queue.java
└── test-cases/
    └── README.md
```

`out/` is created by the compiler and is ignored by Git. The expected test-case
results live only in [`docs/assignment-notes.md`](docs/assignment-notes.md);
`test-cases/` and `report/` are placeholders owned by Member 4.

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

`Main` is a Member 4 stub: it compiles and exits without doing anything yet.
Until `Main` is implemented, the only runnable scheduler is `SjfScheduler`, and
it has to be called from a small throwaway driver (not committed).

## Test cases

The two assignment test cases and their expected results are kept in **one
place only**: [`docs/assignment-notes.md`](docs/assignment-notes.md). That file
also contains the traces, the derived SJF reference results and the open
questions.

- **Test Case 1:** `Q = 3`, `CS = 0`.
- **Test Case 2:** `Q = 3`, `CS = 1`.

Do not duplicate the expected numbers in `test-cases/` or in the report; link
to the assignment notes instead. This avoids two versions of the truth drifting
apart.

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

- `main` is protected by convention: no direct pushes.
- Member 1's base + SJF are already on `main`. The remaining branches are
  `feature/acs-tier1-tier2`, `feature/aging-matrix-metrics` and
  `feature/report-tests`.
- Small commits with clear messages.
- Pull requests need at least one reviewer.
- Pull before you push; resolve conflicts locally, recompile and retest.

The detailed version is in [`docs/git-workflow.md`](docs/git-workflow.md).

## How to add your part

Start from your feature branch and edit only the files you own:

| Member | Branch | Files to edit |
|---|---|---|
| 2 | `feature/acs-tier1-tier2` | `src/scheduler/AcsScheduler.java`, `src/scheduler/Tier2Queue.java` |
| 3 | `feature/aging-matrix-metrics` | `src/scheduler/AgingPolicy.java`, `src/scheduler/MatrixPrinter.java`, `src/scheduler/MetricsCalculator.java` |
| 4 | `feature/report-tests` | `src/scheduler/Main.java`, `test-cases/`, `report/` |

Each stub contains a `TODO (Member X)` comment and throws
`UnsupportedOperationException` until it is implemented. Keep the public class
names and method signatures exactly as they are, because other members call
them.

**Do not change signatures in `Process`, `Scheduler`, `SimulationResult` or
`Snapshot` without telling the team.** These are the shared contracts; changing
them breaks everyone else's branch.

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
- [ ] ACS Tier 1 Round Robin implemented (1 mark)
- [ ] ACS Tier 2 demotion + non-preemptive priority implemented (1 mark)
- [ ] ACS Tier 3 aging implemented (1 mark)
- [ ] Both test cases pass (0.5 mark)
- [ ] Analysis table per test case (0.5 mark)
- [ ] PDF report with CS/Q graphs and Tier 2 trade-offs discussion (1 mark)
- GUI (bonus, Member 1): planned for later, after Preemptive SJF and ACS both work
- [ ] Final ZIP checked and named correctly
