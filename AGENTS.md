# Repository Guide

## Project layout

- `common/` contains shared Kotlin implementation code.
- `rider/` contains Rider-specific Kotlin integration.
- `src/main/resources/META-INF/` contains plugin descriptors.
- `iconGenerator/assets/` is the source for generated SVG resources.
- `src/main/resources/assets/` is generated and ignored by Git.

## Build and validation

- Install JavaScript tooling with `pnpm install --frozen-lockfile` when `node_modules/` is absent.
- `npm run svgo` generates plugin SVG resources. Root Gradle `processResources` runs it automatically; do not manually edit generated assets.
- Use the smallest relevant Gradle task. For Kotlin changes, run:

  ```bash
  ./gradlew compileKotlin --no-daemon
  ```

## Kotlin conventions

- Use two-space indentation, LF line endings, UTF-8, a final newline, and no trailing whitespace.
- Keep Kotlin lines at or below 160 characters.
- Preserve the MIT license header in Kotlin files.
- Prefer clear, small functions and explicit domain names.
- Use expression bodies (`=`) for one-line functions where they remain readable.
- Prefer `when` for conditional logic with multiple branches. Use `if` for a single guard or boolean condition.
- Name arguments when calling a function with more than two parameters, unless the names would add no clarity.
- Group related `val`/`var` declarations together. Separate distinct logic blocks with blank lines, especially before `if`, `return`, or a new method group.
- Prefer guard clauses for early exits.
- Do not introduce broad exception handling or silently discard errors.
- Avoid holding PSI elements or `VirtualFile` instances in long-lived caches. Cache stable identifiers and invalidate cached state explicitly.

## IntelliJ Platform conventions

- Keep `IconProvider` and project-tree decorator callbacks lightweight: do not access file indexes, scan projects, or perform repeated linear work from render paths.
- Use project/application services for shared state. Dispose message-bus connections and invalidate caches on every relevant lifecycle event.
- Preserve project-specific association precedence over global associations.
- Use `DumbAware` paths only with VFS/file-type/configuration data; do not require PSI or indexes during indexing.

## Scope and editing

- Make focused changes; do not reformat unrelated files.
- Keep generated output and IDE metadata out of commits unless explicitly requested.
- Update user-facing text when a settings option or feature is added, renamed, or removed.
