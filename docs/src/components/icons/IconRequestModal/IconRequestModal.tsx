import { useEffect, useRef, useState } from "preact/hooks";
import styles from "./IconRequestModal.module.css";

type Props = {
  open: boolean;
  onClose: () => void;
};

type IconType = "file" | "folder";

type FormState = {
  name: string;
  type: IconType;
  patterns: string;
  referenceUrl: string;
  contextUrl: string;
  colors: string;
  // Honeypot: real users never see or fill this. Bots that auto-fill every
  // field will trip it, so we can silently drop the submission.
  website: string;
};

type Errors = Partial<Record<"name" | "patterns" | "referenceUrl" | "contextUrl", string>>;

const REPO = "AtomMaterialUI/iconGenerator";

const EMPTY_FORM: FormState = {
  name: "",
  type: "file",
  patterns: "",
  referenceUrl: "",
  contextUrl: "",
  colors: "",
  website: "",
};

function isValidUrl(value: string): boolean {
  try {
    const url = new URL(value);
    return url.protocol === "http:" || url.protocol === "https:";
  } catch {
    return false;
  }
}

// Normalise a bullet-point textarea into a clean list, stripping any leading
// bullet markers the user typed themselves.
function toBulletList(raw: string): string[] {
  return raw
    .split("\n")
    .map((line) => line.replace(/^\s*[-*•]\s*/, "").trim())
    .filter((line) => line.length > 0);
}

function patternsLabel(type: IconType): string {
  return type === "folder" ? "Folder names" : "File patterns";
}

function patternsPlaceholder(type: IconType): string {
  return type === "folder"
    ? "- .github\n- node_modules\n- dist"
    : "- *.config.js\n- Dockerfile\n- .env";
}

function validate(form: FormState): Errors {
  const errors: Errors = {};

  if (form.name.trim() === "") {
    errors.name = "Please provide an icon name.";
  }

  if (toBulletList(form.patterns).length === 0) {
    errors.patterns = `Please list at least one ${
      form.type === "folder" ? "folder name" : "file pattern"
    }.`;
  }

  if (form.referenceUrl.trim() !== "" && !isValidUrl(form.referenceUrl.trim())) {
    errors.referenceUrl = "Enter a valid http(s) URL.";
  }

  if (form.contextUrl.trim() !== "" && !isValidUrl(form.contextUrl.trim())) {
    errors.contextUrl = "Enter a valid http(s) URL.";
  }

  return errors;
}

function buildIssueUrl(form: FormState): string {
  const typeLabel = form.type === "folder" ? "Folder" : "File";
  const title = `[Icon Request] ${form.name.trim()} (${typeLabel})`;

  const bullets = toBulletList(form.patterns)
    .map((item) => `- \`${item}\``)
    .join("\n");

  const lines = [
    `### Icon request: ${form.name.trim()}`,
    "",
    `**Icon type:** ${typeLabel}`,
    "",
    `**${patternsLabel(form.type)}:**`,
    bullets,
    "",
    `**Reference icon:** ${form.referenceUrl.trim() || "_none provided_"}`,
    "",
    `**Context / explanation:** ${form.contextUrl.trim() || "_none provided_"}`,
    "",
    `**Suggested colors:** ${form.colors.trim() || "_none provided_"}`,
    "",
    "---",
    "_Submitted from the icon gallery._",
  ];

  const params = new URLSearchParams({
    title,
    body: lines.join("\n"),
    labels: "enhancement",
  });

  return `https://github.com/${REPO}/issues/new?${params.toString()}`;
}

export default function IconRequestModal({ open, onClose }: Props) {
  const dialogRef = useRef<HTMLDialogElement | null>(null);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [errors, setErrors] = useState<Errors>({});

  // Mirror `open` onto the native <dialog>, which gives us Escape-to-close,
  // focus trapping, and the backdrop for free.
  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;

    if (open && !dialog.open) {
      setForm(EMPTY_FORM);
      setErrors({});
      dialog.showModal();
    } else if (!open && dialog.open) {
      dialog.close();
    }
  }, [open]);

  const update = <K extends keyof FormState>(key: K, value: FormState[K]) =>
    setForm((current) => ({ ...current, [key]: value }));

  const handleSubmit = (event: Event) => {
    event.preventDefault();

    // Silently drop bot submissions that filled the hidden honeypot.
    if (form.website.trim() !== "") {
      onClose();
      return;
    }

    const nextErrors = validate(form);
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    window.open(buildIssueUrl(form), "_blank", "noopener,noreferrer");
    onClose();
  };

  return (
    <dialog
      ref={dialogRef}
      class={styles.modal}
      onClose={onClose}
      onClick={(event) => {
        if (event.target === dialogRef.current) {
          onClose();
        }
      }}
    >
      <div class={styles.modalHeader}>
        <h2 class={styles.modalTitle}>Request an icon</h2>
        <button
          type="button"
          class={styles.closeBtn}
          aria-label="Close"
          onClick={onClose}
        >
          &times;
        </button>
      </div>

      <form class={styles.form} onSubmit={handleSubmit} novalidate>
        <p class={styles.intro}>
          Fill in the details below and we'll open a pre-filled issue on GitHub
          for you to review and submit.
        </p>

        <label class={styles.field}>
          <span class={styles.label}>
            Icon name <span class={styles.required}>*</span>
          </span>
          <input
            type="text"
            class={styles.input}
            value={form.name}
            placeholder="e.g. Prettier"
            onInput={(event) =>
              update("name", (event.target as HTMLInputElement).value)
            }
          />
          {errors.name && <span class={styles.error}>{errors.name}</span>}
        </label>

        <label class={styles.field}>
          <span class={styles.label}>
            Icon type <span class={styles.required}>*</span>
          </span>
          <select
            class={styles.select}
            value={form.type}
            onChange={(event) =>
              update("type", (event.target as HTMLSelectElement).value as IconType)
            }
          >
            <option value="file">File</option>
            <option value="folder">Folder</option>
          </select>
        </label>

        <label class={styles.field}>
          <span class={styles.label}>
            {patternsLabel(form.type)} <span class={styles.required}>*</span>
          </span>
          <span class={styles.hint}>One per line, as bullet points.</span>
          <textarea
            class={styles.textarea}
            rows={4}
            value={form.patterns}
            placeholder={patternsPlaceholder(form.type)}
            onInput={(event) =>
              update("patterns", (event.target as HTMLTextAreaElement).value)
            }
          />
          {errors.patterns && (
            <span class={styles.error}>{errors.patterns}</span>
          )}
        </label>

        <label class={styles.field}>
          <span class={styles.label}>Reference icon URL</span>
          <span class={styles.hint}>
            A link to an existing icon we can use as a visual reference.
          </span>
          <input
            type="url"
            class={styles.input}
            value={form.referenceUrl}
            placeholder="https://..."
            onInput={(event) =>
              update("referenceUrl", (event.target as HTMLInputElement).value)
            }
          />
          {errors.referenceUrl && (
            <span class={styles.error}>{errors.referenceUrl}</span>
          )}
        </label>

        <label class={styles.field}>
          <span class={styles.label}>Context URL</span>
          <span class={styles.hint}>
            A link explaining what this {form.type} is (docs, homepage, repo...).
          </span>
          <input
            type="url"
            class={styles.input}
            value={form.contextUrl}
            placeholder="https://..."
            onInput={(event) =>
              update("contextUrl", (event.target as HTMLInputElement).value)
            }
          />
          {errors.contextUrl && (
            <span class={styles.error}>{errors.contextUrl}</span>
          )}
        </label>

        <label class={styles.field}>
          <span class={styles.label}>Colors (optional)</span>
          <input
            type="text"
            class={styles.input}
            value={form.colors}
            placeholder="e.g. #f7b93e, or 'match the brand'"
            onInput={(event) =>
              update("colors", (event.target as HTMLInputElement).value)
            }
          />
        </label>

        {/* Honeypot: visually hidden, off the tab order, ignored by a11y. */}
        <div class={styles.honeypot} aria-hidden="true">
          <label>
            Website
            <input
              type="text"
              tabIndex={-1}
              autocomplete="off"
              value={form.website}
              onInput={(event) =>
                update("website", (event.target as HTMLInputElement).value)
              }
            />
          </label>
        </div>

        <div class={styles.actions}>
          <button type="button" class={styles.secondaryBtn} onClick={onClose}>
            Cancel
          </button>
          <button type="submit" class={styles.primaryBtn}>
            Open GitHub issue
          </button>
        </div>
      </form>
    </dialog>
  );
}
