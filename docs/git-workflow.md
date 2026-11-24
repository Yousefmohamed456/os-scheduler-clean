# Git Workflow

Short version: `main` is protected by convention, each member works on their own
branch, and every change reaches `main` through a reviewed pull request.

> The skeleton repository has no remote yet. The `git pull` / `git push`
> commands below assume a remote named `origin` has been added.

## Branches

- `main` is protected by convention: **nobody pushes directly to it**.
- One branch per member:

| Branch | Owner |
|---|---|
| `feature/acs-tier1-tier2` | Member 2 |
| `feature/aging-matrix-metrics` | Member 3 |
| `feature/report-tests` | Member 4 |

Member 1's shared base + SJF are already on `main`, so there is no separate
SJF branch. The three branches above are created from `main`.

Create your branch from the latest `main`:

```bash
git switch main
git pull
git switch -c feature/<name>
```

## Commits

- Make small commits with clear messages, for example:
  - `Add Tier 2 demotion`
  - `Fix waiting-time formula`
  - `Add Test Case 2 analysis table`
- Commit source and docs only. Never commit `out/`, `*.class`, `.idea/` or
  `*.zip`; they are already in `.gitignore`.

## Pull requests

- Start from your feature branch and run `git merge main` before opening a pull
  request.
- Open a pull request from your feature branch into `main`.
- At least one teammate reviews it before merging.
- Keep the PR small enough to review. If it grows, split it.
- The reviewer checks that the code compiles and that the four contracts in
  [`team-plan.md`](team-plan.md) are respected.

## Pull before you push

```bash
git switch main
git pull
git switch feature/<name>
git merge main
# resolve conflicts if any, compile, and commit
git push
```

## Resolving a simple conflict

1. Run `git pull` (or `git merge main`) and open each conflicted file.
2. Look for the markers:

   ```text
   <<<<<<< HEAD
   your version
   =======
   the other version
   >>>>>>> main
   ```

3. Keep the correct code, remove the three marker lines, and save the file.
4. Compile and run the test cases again.
5. Stage and commit:

   ```bash
   git add <file>
   git commit -m "Resolve merge conflict in <file>"
   ```

6. Push and tell the reviewer what changed.

If the conflict is not simple, stop and ask the other author instead of
guessing. Do not force-push `main`.
