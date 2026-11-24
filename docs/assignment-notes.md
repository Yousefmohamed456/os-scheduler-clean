# Assignment Notes

**Course:** CS241 Operating Systems, Cairo University  
**Assignment:** Assignment 2 - Cloud-Cluster CPU Scheduling  
**Team:** Group `<fill in>`  
**Members:** `<Member 1>`, `<Member 2>`, `<Member 3>`, `<Member 4>`

This file is the shared understanding of the assignment. It repeats the
requirements so nobody has to guess, records what the expected test results
imply, and lists the questions that are still unclear.

---

## 1. What the assignment asks for

### Requirement 1 - Simulator

Write a Java simulator for two schedulers. **Context switching (CS) must be
counted in all preemptive algorithms.**

1. **Preemptive SJF:** the process with the shortest remaining time always runs.
2. **Adaptive Container Scheduler (ACS)**, with three tiers:
   - **Tier 1:** all new processes start in a Round Robin queue with a static
     quantum `Q`.
   - **Tier 2 (demotion):** a process that uses its entire quantum without
     finishing is demoted to a Non-Preemptive Priority queue. Once it starts
     running there, it cannot be preempted until it finishes.
   - **Tier 3 (aging):** to prevent starvation, increase the priority of
     processes waiting in Tier 2 as time passes.
     Rule (from Test Case 2):

     ```text
     aging             = floor(waitTime / 4)
     effectivePriority = priority - aging
     ```

     Lower number = higher priority. `waitTime` counts from the moment the
     process entered Tier 2.

### Requirement 2 - Output

- Print the tier matrix for each time step.
- Print Waiting Time, Turnaround Time and Response Time for every process.
- Print Average Waiting Time and Average Turnaround Time.

Formulas:

```text
Waiting    = Turnaround - Burst      (this includes CS time)
Turnaround = Finish - Arrival
Response   = FirstRun - Arrival
```

### Requirement 3 - PDF report

For each test case, run the simulator with different CS and Q values. Draw
graphs showing the effect of CS and Q on average waiting time and average
turnaround time. Discuss the trade-offs of ACS Tier 2 demotion
(CPU utilization vs Response Time).

### Bonus

A GUI for the simulator.

### Submission and grading

The assignment's submission rules are:

1. Teams must consist of exactly 4 or 5 students from the same lab group or
   with the same TA.
2. One team member submits only the final version files containing the source
   code.
3. Submit one ZIP file named `A2_TAname_Group_ID1_ID2_ID3_ID4`.
4. The deadline is announced on Classroom and no late submission is allowed.
5. Each violation of the instructions or requirements deducts 1 mark.
6. All team members must understand all parts of the code.
7. Cheating receives a negative grade, per the Plagiarism Scope document.

Requirement 3 also says "Submit a PDF report". Submission rule 2 says "only the
final version files containing the source code". The assignment does not say
whether the PDF goes inside the ZIP; this is listed as an open question below.
The repository keeps `report/report.pdf` and its source so it can be submitted
either way.

| Graded item | Marks |
|---|---:|
| SJF | 1 |
| Tier 1 | 1 |
| Tier 2 | 1 |
| Tier 3 (aging) | 1 |
| Test cases | 0.5 |
| Analysis table per test case | 0.5 |
| Report | 1 |
| Bonus (GUI) | 1 |

---

## 2. Test cases and expected results

This section records the expected results used to verify the implementation.
The input files are stored in `test-cases/testcases.txt`; `Main` reads them and
prints the matrices and analysis tables. The assignment's two test cases are
mandatory, and two generated test cases were added as Requirement 3 asks.

### Test Case 1 (Q = 3, CS = 0)

| Process | Arrival | Burst | Priority | Finish | Turnaround | Waiting |
|---|---:|---:|---:|---:|---:|---:|
| P1 | 0 | 4 | 2 | 9 | 9 | 5 |
| P2 | 1 | 7 | 3 | 13 | 12 | 5 |
| P3 | 3 | 2 | 1 | 8 | 5 | 3 |

- Average Turnaround = **8.67**
- Average Waiting = **4.33**

### Test Case 2 (Q = 3, CS = 1)

| Process | Arrival | Burst | Priority | Finish | Turnaround | Waiting |
|---|---:|---:|---:|---:|---:|---:|
| P1 | 0 | 5 | 4 | 18 | 18 | 13 |
| P2 | 1 | 6 | 2 | 15 | 14 | 8 |
| P3 | 4 | 4 | 3 | 20 | 16 | 12 |

- Average Turnaround = **16.00**
- Average Waiting = **11.00**

> These expected results are for **ACS**. The assignment does not give expected
> SJF results, so the team checked SJF separately; the verified references are
> below.

### SJF reference results (verified)

SJF ignores the quantum. These values were produced by the implementation in
`SjfScheduler` and confirmed by hand-tracing.

**Test Case 1 (CS = 0):**

| Process | Arrival | Burst | Finish | Turnaround | Waiting |
|---|---:|---:|---:|---:|---:|
| P1 | 0 | 4 | 4 | 4 | 0 |
| P2 | 1 | 7 | 13 | 12 | 5 |
| P3 | 3 | 2 | 6 | 3 | 1 |

Average Turnaround = **6.33**, Average Waiting = **2.00**.

**Test Case 2 (CS = 1):**

| Process | Arrival | Burst | Finish | Turnaround | Waiting |
|---|---:|---:|---:|---:|---:|
| P1 | 0 | 5 | 5 | 5 | 0 |
| P2 | 1 | 6 | 17 | 16 | 10 |
| P3 | 4 | 4 | 10 | 6 | 2 |

Average Turnaround = **9.00**, Average Waiting = **4.00**.

### Generated Test Case 3 (Q = 2, CS = 1)

| Process | Arrival | Burst | Priority | ACS Finish | ACS Turnaround | ACS Waiting |
|---|---:|---:|---:|---:|---:|---:|
| P1 | 0 | 6 | 5 | 16 | 16 | 10 |
| P2 | 1 | 4 | 4 | 19 | 18 | 14 |
| P3 | 2 | 2 | 3 | 8 | 6 | 4 |
| P4 | 4 | 2 | 2 | 11 | 7 | 5 |

ACS Average Turnaround = **11.75**, Average Waiting = **8.25**.
SJF Average Turnaround = **8.50**, Average Waiting = **5.00**.

### Generated Test Case 4 (Q = 2, CS = 1)

| Process | Arrival | Burst | Priority | ACS Finish | ACS Turnaround | ACS Waiting |
|---|---:|---:|---:|---:|---:|---:|
| P1 | 1 | 5 | 3 | 16 | 15 | 10 |
| P2 | 2 | 4 | 5 | 21 | 19 | 15 |
| P3 | 4 | 3 | 1 | 18 | 14 | 11 |
| P4 | 8 | 2 | 2 | 12 | 4 | 2 |

ACS Average Turnaround = **13.00**, Average Waiting = **9.50**.
SJF Average Turnaround = **8.00**, Average Waiting = **4.50**.

**Extra edge cases (used for verification):**

| Case | Input (arrival, burst) | CS | Finish | Turnaround | Waiting | Avg TAT | Avg WT |
|---|---|---:|---|---|---|---:|---:|
| Preemption + CS | P1(0, 8), P2(2, 2), P3(3, 1) | 1 | 14, 7, 4 | 14, 5, 1 | 6, 3, 0 | 6.67 | 3.00 |
| Idle gap | P1(0, 1), P2(5, 2) | 1 | 1, 7 | 1, 2 | 0, 0 | 1.50 | 0.00 |
| Same-arrival tie | P3(0, 2), P2(0, 2), P1(0, 4) | 0 | 4, 2, 8 | 4, 2, 8 | 2, 0, 4 | 4.67 | 2.00 |

The same-arrival case also checks that the result keeps the **original input
order** (`P3, P2, P1`), not the run order.

---

## 3. How the expected results were obtained

These traces are derived from the expected finish times. They are not extra
requirements; they are a check that our understanding matches the assignment.

### Test Case 1 trace

```text
T=0-3    P1 runs 3 of its 4 units (Q=3). Remaining 1 -> demoted to Tier 2 at T=3.
T=3-6    P2 runs 3 of its 7 units. Remaining 4 -> demoted to Tier 2 at T=6.
         P3 is still in Tier 1, so Tier 1 continues.
T=6-8    P3 runs its last 2 units and finishes at T=8.
T=8-9    Tier 2: P1 waited 5 (aging 1, effective priority 1);
         P2 waited 2 (aging 0, effective priority 3).
         P1 runs its last unit and finishes at T=9.
T=9-13   P2 runs its last 4 units and finishes at T=13.
```

Derived response times (not listed in the assignment): P1 = 0, P2 = 2, P3 = 3.

### Test Case 2 trace

```text
T=0-3    P1 runs 3 of its 5 units (Q=3). Remaining 2 -> demoted at T=3.
T=3-4    Context switch (1 unit).
T=4-7    P2 runs 3 of its 6 units. Remaining 3 -> demoted at T=7.
T=7-8    Context switch (1 unit).
T=8-11   P3 runs 3 of its 4 units. Remaining 1 -> demoted at T=11.
T=11-12  Context switch (1 unit).
         At T=11: P1 waited 8 -> 4-2 = 2;
                  P2 waited 4 -> 2-1 = 1;
                  P3 waited 0 -> 3.
         P2 has the best effective priority.
T=12-15  P2 runs its last 3 units and finishes at T=15.
T=15-16  Context switch (1 unit).
         At T=15: P1 waited 12 -> 4-3 = 1;
                  P3 waited 4 -> 3-1 = 2.
         P1 runs next.
T=16-18  P1 runs its last 2 units and finishes at T=18.
T=18-19  Context switch (1 unit).
T=19-20  P3 runs its last unit and finishes at T=20.
```

Derived response times (not listed in the assignment): P1 = 0, P2 = 3, P3 = 4.

### Typos and inconsistencies in the assignment

The expected finish times and the aging formula are treated as authoritative.
The following rows do not agree with them; the simulator prints the corrected
values.

1. **Test Case 1, last matrix row:** the assignment jumps from `T=12` to
   `T=14` and shows `Tier 2: [Empty]`. P2 finishes at `T=13`, so the last
   meaningful row is `T=13`. The `T=13` row is printed, and `T=14` is not.
2. **Test Case 1, T = 8:** the assignment shows P1 in Tier 2 with
   `Priority: 2`. P1 entered Tier 2 at `T=3` and waited 5 units, so
   `aging = floor(5/4) = 1` and the effective priority is `2 - 1 = 1`. The
   simulator prints the effective priority.
3. **Test Case 2, T = 4:** the assignment shows P1 in Tier 2 with
   `Priority: 2`. P1 entered Tier 2 at `T=3` and waited 1 unit, so
   `aging = 0` and the effective priority is `4`. The value 2 is not
   consistent with the formula.
4. **Test Case 2, T = 7:** the assignment shows P1 with `Priority: 4`.
   P1 waited from `T=3` to `T=7`, so `aging = 1` and the effective priority
   is `3`. The simulator prints the effective priority.
5. **Test Case 2, T = 11:** the assignment says
   "Context switch loads P1". At `T=11`:
   P1 waited 8 -> `4 - 2 = 2`; P2 waited 4 -> `2 - 1 = 1`;
   P3 waited 0 -> `3`. P2 has the best effective priority, and the finish
   times (P2 = 15, P1 = 18, P3 = 20) confirm that P2 runs from 12 to 15.
   The simulator loads P2.
6. **Test Case 2, missing rows:** the assignment leaves `T=12`, `T=16` and
   `T=19` blank, and omits the context-switch text at `T=15`. The simulator
   fills every time step and prints the switch text on the rows where the
   switch happens.

The assignment's Tier 2 display is also inconsistent about priority: some rows
show the base priority (for example Test Case 1 at `T=8` and Test Case 2 at
`T=7`) while other rows show an aged value. This implementation always shows
the effective priority in the matrix and prints the full aging calculation
before each Tier 2 selection, so the matrix and the aging lines agree.

---

## 4. Assumptions used by the implementation

These points are not spelled out in the assignment text. The expected results
imply most of them, and the rest are the most natural choices. They are repeated
in Open questions for confirmation.

1. **Tier 1 is served before Tier 2.** In Test Case 1, P1 is already in Tier 2
   at `T=3`, but P2 and P3 still run in Tier 1 first. Tier 2 is only served when
   Tier 1 has no ready process.
2. **No context switch before the first process.** In Test Case 2, P1 starts at
   `T=0`; the first 1-unit CS appears between P1 and P2, not before P1.
3. **No context switch after the last process.** P3 finishes at `T=20`, not
   `T=21`, so the final switch is not charged.
4. **A context switch is charged when the CPU moves to a different process.**
   With `CS=1`, every change of running process costs one time unit.
5. **Demotion happens after a full quantum that does not finish the process.**
   The process is moved to Tier 2 at the time its quantum ends.
6. **Tier 2 selection uses the aging values at the moment the CPU becomes
   free.** In Test Case 2 the decision is made at `T=11`, before the CS gap, and
   the process runs after the gap.
7. **A Tier 2 process is never preempted.** Once selected, it runs until it
   finishes. New arrivals cannot interrupt it.
8. **A context switch is only charged when the next process is different.**
   Re-running the same process does not cost a switch. No CS is charged at
   `t = 0`, after an idle period, or after the last process.
9. **A context switch after a completion is charged.** When a process finishes
   and another process follows, the CS time is counted before the next process
   starts. This applies to both schedulers.
10. **Tier 2 order is effective priority, then Tier 2 entry time, then id.**
    A lower effective priority runs first. If two processes tie, the one that
    entered Tier 2 earlier runs first; if they also tie, the smaller id runs
    first.
11. **Aging is evaluated when the next process is chosen.** The wait time is
    `currentTime - tier2EntryTime`, so the values printed before a selection are
    the values used for that selection.
12. **The tier matrix shows effective priority in Tier 2.** The assignment is
    inconsistent about this (see the typos section); the implementation shows
    the value that the selection actually uses.
13. **Finishing exactly at the quantum boundary does not demote.** The process
    has no remaining time, so it completes instead of entering Tier 2.

---

## 5. Open questions

These are genuinely unclear from the assignment text. Do not silently guess;
ask the TA and update this section.

### Simulator behaviour

1. **CS after a finished process (SJF):** when a process finishes, should SJF
   charge a context switch before the next process starts? The repo does charge
   it, except when the CPU goes idle. The assignment text does not state this
   for SJF.
2. **No CS at t = 0 (SJF):** confirm that the first process starts at its
   arrival time without paying a context switch. The repo charges no CS at
   `t = 0`.
3. **SJF tie-break:** when two ready processes have the same remaining time,
   what order should be used? The repo uses earlier arrival, then id.
4. **Tier 1 vs Tier 2 order:** confirm that Tier 1 is strictly served before
   Tier 2 whenever a Tier 1 process is ready. Both test cases imply this, but
   the assignment text does not say it explicitly.
5. **CS at the edges (ACS):** confirm that no CS is charged before the first
   process or after the last process. The Test Case 2 finish times imply this.
6. **Finishing exactly at the quantum boundary:** if a process uses exactly
   `Q` units and finishes, is it still "demoted"? The tests do not cover this.
   The repo assumes no demotion because the process is already finished.
7. **Aging clamp:** should `effectivePriority` be clamped at 0, or may it become
   negative? The repo does **not** clamp. Confirm.
8. **Aging wait time while running:** does `waitTime` keep growing while a Tier 2
   process is actually running? Because Tier 2 is non-preemptive, the process
   finishes before another Tier 2 selection, so this should not matter. Confirm
   there is no edge case.
9. **SJF output:** is the tier matrix required for SJF too, or only for ACS?
   The repo leaves SJF snapshots empty because tiers are an ACS concept.
10. **Input method:** the assignment does not define an input format. The
    implementation reads `test-cases/testcases.txt`, which contains the two
    assignment cases and the two generated cases. Confirm that a text file is
    acceptable.
11. **Generated test cases:** Requirement 3 says "using your generated test
    cases". The implementation adds exactly two generated cases (Test Case 3
    and Test Case 4). Confirm that two is enough and that the assignment's two
    cases should also be reported.
12. **Matrix priority display:** the assignment's Tier 2 rows mix base and aged
    priorities. The implementation prints the effective priority. Confirm that
    the effective value is the intended display.

### Report and submission

13. **Report details:** is there a page limit, a preferred graph tool, and a
    required report file name? The assignment only specifies the ZIP name.
14. **PDF location:** Requirement 3 says "Submit a PDF report", but submission
    rule 2 says "only the final version files containing the source code".
    Should the PDF be submitted separately, or included in the ZIP anyway?
15. **ZIP name:** what exactly does `Group` mean - the group number, the group
    name, or both? Are the four student IDs separated by underscores in the
    order shown?
16. **GUI bonus:** is any working GUI enough, or are specific screens/features
    required?
17. **Analysis table:** should the per-test-case analysis table be inside the
    PDF report, a separate spreadsheet, or both?
