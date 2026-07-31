import { useEffect, useRef } from "preact/hooks";
import type { IconCategory } from "../../lib/icons";

type Props = {
  icon: IconCategory | null;
  onClose: () => void;
};

function examplesLabel(category: string): string {
  return category.includes("folder") ? "Folders" : "Examples";
}

function firstExamples(fileNames: string): string {
  const parts = fileNames.split(",");
  const shown = parts.slice(0, 3).join(", ");
  return parts.length > 3 ? `${shown}...` : shown;
}

export default function IconModal({ icon, onClose }: Props) {
  const dialogRef = useRef<HTMLDialogElement | null>(null);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;

    if (icon && !dialog.open) {
      dialog.showModal();
    } else if (!icon && dialog.open) {
      dialog.close();
    }
  }, [icon]);

  const association = icon?.association ?? null;

  return (
    <dialog
      ref={dialogRef}
      class="modal"
      onClose={onClose}
      onClick={(event) => {
        if (event.target === dialogRef.current) {
          onClose();
        }
      }}
    >
      {icon && (
        <>
          <div class="modal-header">
            <h2 class="modal-title">{icon.name} Icon</h2>
            <button
              type="button"
              class="close-btn"
              aria-label="Close"
              onClick={onClose}
            >
              &times;
            </button>
          </div>

          <div class="modal-content">
            <div class="icon-preview">
              <img src={icon.path} alt={icon.name} />
              <div class="icon-info">
                <h3>{icon.name}</h3>
                <span class="category-badge">{icon.category}</span>
              </div>
            </div>

            {association ? (
              <div class="association-info">
                <h4 class="association-title">Association Details</h4>

                <div class="info-item">
                  <span class="info-label">Name:</span>
                  <span class="info-value">{association.name}</span>
                </div>

                <div class="info-item">
                  <span class="info-label">Type:</span>
                  <span class="info-value">
                    {association.type === "regex"
                      ? "Pattern-based"
                      : "Type-based"}
                  </span>
                </div>

                <div class="info-item">
                  <span class="info-label">Priority:</span>
                  <span class="info-value">
                    <span class="priority-badge">{association.priority}</span>
                  </span>
                </div>

                <div class="info-item">
                  <span class="info-label">Color:</span>
                  <span class="info-value">
                    {association.iconColor}
                    {association.iconColor !== "inherit" && (
                      <span
                        class="color-swatch"
                        style={{ backgroundColor: association.iconColor }}
                      />
                    )}
                  </span>
                </div>

                {association.folderIconColor && (
                  <div class="info-item">
                    <span class="info-label">Folder Icon Color:</span>
                    <span class="info-value">
                      {association.folderIconColor}
                      <span
                        class="color-swatch"
                        style={{ backgroundColor: association.folderIconColor }}
                      />
                    </span>
                  </div>
                )}

                <div class="info-item">
                  <span class="info-label">Pattern:</span>
                  <span class="info-value">
                    <code class="pattern-code">{association.pattern}</code>
                  </span>
                </div>

                {association.fileNames && association.fileNames !== "N/A" && (
                  <div class="info-item">
                    <span class="info-label">
                      {examplesLabel(icon.category)}:
                    </span>
                    <span class="info-value info-examples">
                      {firstExamples(association.fileNames)}
                    </span>
                  </div>
                )}
              </div>
            ) : (
              <div class="no-association">
                <p>No association data available for this icon.</p>
                <p class="no-association-hint">
                  This icon may be used for UI elements or custom associations.
                </p>
              </div>
            )}
          </div>
        </>
      )}
    </dialog>
  );
}
