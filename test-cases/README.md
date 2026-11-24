# Test Cases

The input file is [`testcases.txt`](testcases.txt). It is read by
`java -cp out scheduler.Main`.

## File format

```text
CASE|<case name>
Q|<quantum>
CS|<context-switch time>
PROCESS|<id>|<arrival>|<burst>|<priority>
```

One `CASE` block per test case. `PROCESS` lines may repeat. Blank lines are
ignored.

## Cases

| Case | Source | Q | CS | Processes |
|---|---|---:|---:|---|
| Test Case 1 | Assignment | 3 | 0 | P1(0,4,2), P2(1,7,3), P3(3,2,1) |
| Test Case 2 | Assignment | 3 | 1 | P1(0,5,4), P2(1,6,2), P3(4,4,3) |
| Test Case 3 | Generated | 2 | 1 | P1(0,6,5), P2(1,4,4), P3(2,2,3), P4(4,2,2) |
| Test Case 4 | Generated | 2 | 1 | P1(1,5,3), P2(2,4,5), P3(4,3,1), P4(8,2,2) |

Test Case 3 exercises aging changing the Tier 2 choice. Test Case 4 includes
an idle gap at the start and a late arrival.

The expected results, traces and assignment typos are in
[`../docs/assignment-notes.md`](../docs/assignment-notes.md). The analysis
tables and graphs are in [`../report/report.pdf`](../report/report.pdf).
