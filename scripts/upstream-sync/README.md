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

```bash
# Recommended: authenticate to raise the GitHub API rate limit.
export GITHUB_TOKEN="$(gh auth token)"

# Print a report (no writes).
npm run check-upstream

# Update state.json and write a Markdown report file.
node scripts/upstream-sync/check-upstream.mjs --write-state --report upstream-report.md
```

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

`.github/workflows/upstream-sync.yml` runs weekly (and on demand). It executes
the script, uploads the report as an artifact, creates/updates a single
`upstream-sync`-labeled tracking issue with the latest report, and commits the
refreshed `state.json`.
