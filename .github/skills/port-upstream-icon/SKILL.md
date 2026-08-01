---
name: port-upstream-icon
description: Port or dismiss an upstream icon request in this repo. Reads the repo's "Upstream icon sync report" issue (the source of truth for what needs porting) and uses scripts/upstream-sync/state.json only to track handled items. Ports by adding an icon/folder association + SVG, or dismisses an item by marking it ignored so future reports stop surfacing it. Use when the user runs /port-upstream-icon <item>, or asks to port/add/dismiss/skip/ignore an upstream icon request, PR, or issue.
---

You are porting an **upstream icon request** from [
`material-extensions/vscode-material-icon-theme`](https://github.com/material-extensions/vscode-material-icon-theme)
into this repository (`AtomMaterialUI/a-file-icon-idea`).

The user gives you **one argument** — the icon request to act on. It may be:

- a name / title fragment: `zod`, `tensorflow folder`, `keras`
- an upstream number: `3552` (matches the item's upstream issue/PR number)
- a `state.json` key: `pr-3550` or `issue-3552`

**Two modes** — pick based on the user's intent:

- **Port** (default): add the association + SVG for the item.
- **Dismiss**: mark the item handled *without* porting it — when the user says
  "dismiss", "skip", "ignore", "mark as done/handled", or the request isn't something this repo wants to add. This just records the decision
  in `state.json` so future sync reports stop surfacing it. See **"Dismissing an item"** below and skip the porting steps.

For a **port**, your job is: find the item **in the repo's "refresh upstream icon sync state" PR**, gather its icon details from upstream,
and add the correct association (file **or** folder) plus its SVG asset, following this repo's conventions exactly. Work only inside the
repo root and its children.

> This is advisory automation over heuristic data. When the target item is
> ambiguous, or upstream details are missing/unclear, **stop and ask** rather
> than guessing. Never invent an SVG.

## 1. Find the item in the sync report (source of truth)

The **source of truth for what needs porting** is the repo's rolling **"refresh upstream icon sync state" PR**. Its body is a Markdown
report grouping upstream items into *Likely missing*, *Needs review*, and *Likely already present*, each row carrying the upstream `Type`
(issue/pr), `#`, `Title`, and a `Link` to the upstream item.

1. Locate the report issue and read its body:
   ```bash
   gh issue list --repo AtomMaterialUI/a-file-icon-idea --state open \
     --search "refresh upstream icon sync state in:title" \
     --json number,title,updatedAt,url
   gh issue view <n> --repo AtomMaterialUI/a-file-icon-idea --json title,updatedAt,body
   ```
   Pick the most recently updated open issue titled "refresh upstream icon sync state". (If no such issue exists, fall back to the rolling
   `automation/upstream-sync-state` PR body, which carries the same report:
   `gh pr list --repo AtomMaterialUI/a-file-icon-idea --head automation/upstream-sync-state --state all --json number,body,url`.)
2. Match the argument against the report rows (by title fragment, upstream number, or type+number). Capture the upstream **type**,
   **number**, **title** and **link**, and note which section it was in (*missing* / *review* / *present*). If it sits under *Likely already
   present*, warn the user it may already exist and confirm before continuing. If nothing matches, tell the user it's not in the current
   report and stop.

## 2. Confirm it wasn't already handled (state.json guard)

`scripts/upstream-sync/state.json` is **only** used here as a dedup guard — it is *not* the source of truth for what to port. Records are
keyed `pr-<n>` / `issue-<n>` with a human-owned `status` (`pending` / `ported` / `ignored`).

- Look up the item's `issue-<n>` / `pr-<n>` record. If `status` is already
  `ported` or `ignored`, tell the user it's already handled and stop unless they insist on redoing it.
- Also sanity-check the target isn't already in the association XMLs (`grep -in "<name>" iconGenerator/*.xml`); if it is, report that and
  confirm before proceeding.

Report what you resolved: the upstream key, title, link, report section, and `state.json` status — then continue.

## 3. Decide file vs folder association

Determine the association type from the item title/body:

- **Folder** association → the title mentions "folder" (e.g. `add tensorflow folder icon`). Target: `iconGenerator/folder_associations.xml`,
  with **two** SVGs: a closed variant in `iconGenerator/assets/icons/folders/` and a matching open variant in
  `iconGenerator/assets/icons/foldersOpen/` (see step 5).
- **File / language** association (the default) → everything else. Target: `iconGenerator/icon_associations.xml`, SVG dir
  `iconGenerator/assets/icons/files/`.

If a request implies both, ask the user which to port (or do both, one entry each).

## 4. Gather the icon details from upstream

Use `gh` (respects `GITHUB_TOKEN`) against the upstream repo
`material-extensions/vscode-material-icon-theme`, following the report row's
`Link` (its `Type` tells you issue vs PR).

- For a PR, inspect its diff — it contains the new SVG and the mapping:
  `gh pr diff <n> --repo material-extensions/vscode-material-icon-theme`
- For an issue, read it: `gh issue view <n> --repo material-extensions/vscode-material-icon-theme`. Issues often lack an SVG; if so, tell
  the user an SVG is required and ask them to supply one (or the source PR) before writing the association.

From upstream you need:

- **name** — human name (e.g. `TensorFlow`, `Keras`).
- **fileNames / folderNames** — the extensions, filenames or folder names the icon maps to (from the upstream `fileIcons`/`folderIcons`
  definition or the request body). Comma-separated, no spaces.
- **SVG** — the raw `<svg>…</svg>` markup. Take it from the PR diff / upstream
  `icons/<name>.svg`. Do **not** fabricate one.
- **color** — a reasonable brand `iconColor` (6-hex, no `#`); pick from the SVG or the request. When unsure, ask.

## 5. Add the SVG asset

Save the raw SVG to the source assets dir (never the generated
`src/main/resources/assets/`, which is git-ignored and produced by `npm run svgo`):

- File icon → `iconGenerator/assets/icons/files/<name>.svg`
- Folder icon → **two files are required**, both named `<name>.svg`:
  - Closed: `iconGenerator/assets/icons/folders/<name>.svg`
  - Open: `iconGenerator/assets/icons/foldersOpen/<name>.svg`

Use a lowercase, camelCase-or-plain filename consistent with neighbors (e.g. `tensorflow.svg`, `githubActions.svg`). Do not overwrite an
existing file without confirming.

### Folder SVG structure (closed + open)

Folder icons in this repo are **standalone composite SVGs**, not auto-generated from a file icon. Each is a `24 24` viewBox SVG containing
two parts:

1. **Folder shell** — a `<path>` carrying `data-folderColor="<Name>"`, filled with the `folderColor`. The shell path differs between the two
   variants (copy the exact paths from any existing pair, e.g. `folders/gitlab.svg` and `foldersOpen/gitlab.svg`):
   - Closed: `m10 4h-6c-1.11 0-2 .89-2 2v12c0 1.097.903 2 2 2h16c1.097 0 2-.903 2-2v-10c0-1.11-.9-2-2-2h-8l-2-2z`
   - Open: `M20,18H4V8H20M20,6H12L10,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V8C22,6.89 21.1,6 20,6Z`
2. **Brand glyph** — a `<path>` (or `<g>`) carrying `data-folderIconColor="<Name>"`, filled with the `folderIconColor`, positioned
   bottom-right with a `transform` (e.g. `scale(0.45) translate(25 20)` for a 24-viewBox logo). This is where the sourced brand SVG's path
   goes — never fabricate it.

**The open and closed variants are identical except for the folder-shell path** — same glyph, same `transform`, same colors, same MIT header.
Build the closed one, then produce the open one by swapping only the shell path. Both must exist or the open state renders blank.

## 6. Add the association entry

Insert a new `<regex>` element into the correct XML, in the correct **alphabetical region** (both files are organized by `<!--region X-->` …
`<!--endregion-->` comment blocks, plus a `Custom` region in the file XML). Match the surrounding indentation exactly (two-space steps; the
file XML uses `    ` for entries, the folder XML uses `        `). Keep lines ≤160 chars.

**File / language icon** — add to `iconGenerator/icon_associations.xml`:

```xml

<regex fileNames="*.keras,keras.json"
       name="Keras"
       priority="1"
       iconType="FILE"
       pattern=".*\.keras$"
       iconColor="D00000"
       icon="/icons/files/keras.svg"/>
```

- `fileNames`: comma-separated globs/filenames.
- `pattern`: a JS-style regex matching the filename (s); anchor with `$`.
- `priority`: `1` for generic extensions; higher (e.g. `100`, `1000`) for specific full filenames that must win over generic ones.
- `icon`: `/icons/files/<name>.svg`.

**Folder icon** — add to `iconGenerator/folder_associations.xml`:

```xml

<regex folderNames="tensorflow,.tensorflow,_tensorflow"
       name="TensorFlow"
       folderColor="FF6F00"
       folderIconColor="FFE0B2"
       priority="100"
       iconType="FOLDER"
       pattern="^[\._]?tensorflow$"
       icon="/tensorflow.svg"
       defaultState="false"/>
```

- `folderNames`: include the `.`/`_` prefixed variants like existing entries.
- `pattern`: `^[\._]?<name>$` (optionally handle plural/aliases with `(...)`).
- `folderColor` is the folder fill; `folderIconColor` a lighter accent.
- Folder `icon` paths are `/<name>.svg` (no `/icons/folders/` prefix). The single `icon` attribute covers **both** variants — the generator
  resolves the closed SVG from `folders/` and the open SVG from `foldersOpen/` by the same filename, so both source files must be present.
- `priority` is typically `100`; keep `defaultState="false"` like neighbors.

Preserve existing precedence — don't reorder or renumber unrelated entries. Do not touch the MIT license header comment.

## 7. Update state.json

Mark the item handled so the next sync (and this guard) stops surfacing it: set the matching `issue-<n>` / `pr-<n>` record's `status` to
`ported` in
`scripts/upstream-sync/state.json` (leave other fields alone; the sync script maintains `classification`/`lastSeen` and won't downgrade a
human `ported`). Keep the file's 2-space JSON formatting. If the item has no record yet, you may leave state.json untouched — the report
issue remains the source of truth and the next sync will record it.

## 8. Validate and report

- Confirm the XML is well-formed and the new `icon` path matches the saved SVG filename. For a **folder** icon, also confirm **both** the
  closed (`folders/<name>.svg`) and open (`foldersOpen/<name>.svg`) source files exist and are well-formed. A quick check:
  `npm run check-upstream` should now classify the item as `present` (needs `GITHUB_TOKEN`); this is optional and network-dependent.
- You do **not** need to run `npm run svgo` or Gradle — `processResources`
  regenerates `src/main/resources/assets/` from these sources at build time.
- Summarize for the user: the item ported, file vs folder, the SVG path, the XML entry added, and the `state.json` status change. List
  anything you had to guess (color, pattern) so they can adjust. Do not commit unless asked.

## Dismissing an item

When the user wants to **dismiss** an item (skip/ignore/mark handled without porting), do only this — no SVG, no association edits:

1. Resolve the item via **step 1** (the sync report issue) so you have its upstream `type`, `number`, `title` and `link`.
2. In `scripts/upstream-sync/state.json`, set the item's `issue-<n>` / `pr-<n>`
   record `status` to **`ignored`** (use `ported` only if it was actually added here). The reconciler preserves human-set status and never
   downgrades it, so the item stays suppressed from future reports.
3. **If no record exists yet** (the report can list upstream items not yet written to state.json — e.g. issues, when the committed state
   only has PRs), create one so the dismissal sticks. Match the existing record shape exactly:
   ```json
   "issue-3552": {
     "type": "issue",
     "number": 3552,
     "title": "[Icon Request]: Zod",
     "url": "https://github.com/material-extensions/vscode-material-icon-theme/issues/3552",
     "status": "ignored",
     "classification": "missing",
     "firstSeen": "<now ISO-8601 UTC>",
     "lastSeen": "<now ISO-8601 UTC>"
   }
   ```
   Keep the file's 2-space JSON formatting with a trailing newline (matching how the sync script writes it, so the diff is just the one
   added/edited record).
4. Report the dismissal: item key, title, and new `status`. Note the change lands in `state.json`; committing/opening a PR is up to the user
   (the weekly sync also commits state via its rolling PR).

## Key principles

- **One item per invocation** — port or dismiss exactly the requested item.
- **Dismiss = state only** — mark it `ignored` in `state.json`; touch nothing else.
- **Never fabricate an SVG** — get it from upstream or ask the user.
- **Folder icons need two SVGs** — a closed variant in `folders/` and a matching open variant in `foldersOpen/`, identical except the
  folder-shell path; both must exist.
- **Respect conventions** — alphabetical region, indentation, `priority`
  precedence, `.`/`_` folder-name variants, ≤160-char lines, MIT header intact.
- **Ask when ambiguous** — unresolved match, missing SVG, or uncertain color.
- **Edit sources only** — never edit generated `src/main/resources/assets/`.
