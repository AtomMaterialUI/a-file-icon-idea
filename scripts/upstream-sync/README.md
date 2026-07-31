# Upstream Icon Sync

Tracks icon-related **issues** and **pull requests** from the upstream
[`material-extensions/vscode-material-icon-theme`](https://github.com/material-extensions/vscode-material-icon-theme)
project and reports which of them do **not** yet appear to be ported into this
repository's association files:

- `iconGenerator/icon_associations.xml`
- `iconGenerator/folder_associations.xml`

A committed [`state.json`](./state.json) remembers what has already been seen and
handled, so repeat runs only surface **new** or **still-unresolved** items
instead of re-reporting everything.

## Running locally

The tool is a small pnpm package under `scripts/upstream-sync/`. Install its
dependencies once:

```bash
cd scripts/upstream-sync
pnpm install --frozen-lockfile
```

Then run it from the repository root:

```bash
# Recommended: authenticate to raise the GitHub API rate limit.
export GITHUB_TOKEN="$(gh auth token)"

# Print a report (no writes).
npm run check-upstream

# Update state.json and write a Markdown report file.
node scripts/upstream-sync/check-upstream.mjs --write-state --report upstream-report.md
```

## Project layout

The logic is split into small, single-purpose modules under `src/`:

| Module | Responsibility |
| --- | --- |
| `check-upstream.mjs` | Thin entry point; delegates to `src/cli.mjs`. |
| `src/cli.mjs` | Orchestrates one run (args → index → fetch → reconcile → report). |
| `src/config.mjs` | Paths, upstream coordinates and the tokenizer stop-word list. |
| `src/args.mjs` | Command-line parsing (`commander`) with env-var defaults. |
| `src/associations.mjs` | Builds the token index from the XMLs (`fast-xml-parser`). |
| `src/classify.mjs` | Title tokenization and the missing/review/present heuristic. |
| `src/github.mjs` | Upstream issue/PR fetching via the Search API (`@octokit/rest`), asserting each query's result count against `total_count` so a truncated response fails instead of silently dropping tracked items, + relevance filter. |
| `src/state.mjs` | Loads, reconciles and persists `state.json`. |
| `src/report.mjs` | Renders the Markdown report. |

### Flags

| Flag | Description |
| --- | --- |
| `--write-state` | Persist new / updated records back to `state.json`. |
| `--report <path>` | Write the Markdown report to a file (also printed to stdout). |
| `--include-closed` | Also scan closed/merged upstream items. |
| `--limit <n>` | Only process the first `n` fetched items (useful for quick tests). |

## How items are classified

For each upstream item the script derives a **name** from its title (stripping
the `[Icon Request]:` prefix and conventional-commit prefixes like `feat:`),
then matches that name against an index built from the `name`, `fileNames`,
`folderNames` and `icon` attributes of the association XML files:

- **missing** — no match found; likely needs porting.
- **review** — only a weak/ambiguous match; needs a human look.
- **present** — the name matches an existing association; likely already ported.

Matching is **heuristic and advisory**. It intentionally ignores issue/PR
bodies (too noisy) and can produce false positives/negatives for renamed or
aliased icons. Always confirm before acting.

## Marking items as handled

`state.json` records are keyed by `issue-<n>` / `pr-<n>`. Each record has a
human-owned `status`:

- `pending` — default; still shown in reports.
- `ported` — the icon/association has been added here; suppressed from reports.
- `ignored` — intentionally skipped; suppressed from reports.

To resolve an item, edit its `status` in `state.json` and commit. The script
updates `classification`, `title` and `lastSeen` on each run but never
downgrades a human-set `ported`/`ignored` back to `pending`.

## Automation

`.github/workflows/upstream-sync.yml` runs weekly (and on demand). It is built
from marketplace actions and a single npm script — no inline shell logic:

1. `actions/checkout` (with submodules) + `pnpm/action-setup` + `actions/setup-node`.
2. `pnpm install --frozen-lockfile` inside `scripts/upstream-sync`.
3. `npm run check-upstream -- --write-state --report upstream-report.md`
   (the `include_closed` dispatch input is passed via the `INCLUDE_CLOSED` env
   var, which the script reads directly).
4. `actions/upload-artifact` publishes `upstream-report.md`.
5. `peter-evans/create-pull-request` commits the refreshed `state.json` to a
   fixed `automation/upstream-sync-state` branch and opens (or updates) a single
   rolling pull request whose **body is the report** (`body-path`). Because
   `master` is protected, changes land via this PR rather than a direct push.

> **Repo setting required:** enable
> _Settings → Actions → General → "Allow GitHub Actions to create and approve
> pull requests"_ so the workflow's `GITHUB_TOKEN` can open the state PR.
