import { useEffect, useRef } from "preact/hooks";
import type { IconCategory } from "../../../lib/icons";
import styles from "./IconModal.module.css";

type Props = {
  icon: IconCategory | null;
  onClose: () => void;
};

function examplesLabel(category: string): string {
  return category.includes("folder") ? "Folders" : "Examples";
}

function firstExamples(fileNames: string): string {
  const numParts = 6;
  const parts = fileNames.split(",");
  const shown = parts.slice(0, numParts).join(", ");
  return parts.length > numParts ? `${shown}...` : shown;
}

export default function IconModal({ icon, onClose }: Props) {
  const dialogRef = useRef<HTMLDialogElement | null>(null);

  // Native <dialog>.showModal() gives us Escape-to-close and the backdrop for
  // free; the `close` event (Escape, close button, or backdrop click) bubbles
  // up through onClose.
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
      class={styles.modal}
      onClose={onClose}
      onClick={(event) => {
        if (event.target === dialogRef.current) {
          onClose();
        }
      }}
    >
      {icon && (
        <>
          <div class={styles.modalHeader}>
            <h2 class={styles.modalTitle}>{icon.name} Icon</h2>
            <button
              type="button"
              class={styles.closeBtn}
              aria-label="Close"
              onClick={onClose}
            >
              &times;
            </button>
          </div>

          <div class={styles.modalContent}>
            <div class={styles.iconPreview}>
              <img src={icon.path} alt={icon.name} />
              <div class={styles.iconInfo}>
                <h3>{icon.name}</h3>
                <span class={styles.categoryBadge}>{icon.category}</span>
              </div>
            </div>

            {association ? (
              <div class={styles.associationInfo}>
                <h4 class={styles.associationTitle}>Association Details</h4>

                <div class={styles.infoItem}>
                  <span class={styles.infoLabel}>Name:</span>
                  <span class={styles.infoValue}>{association.name}</span>
                </div>

                <div class={styles.infoItem}>
                  <span class={styles.infoLabel}>Type:</span>
                  <span class={styles.infoValue}>
                    {association.type === "regex"
                      ? "Pattern-based"
                      : "Type-based"}
                  </span>
                </div>

                <div class={styles.infoItem}>
                  <span class={styles.infoLabel}>Priority:</span>
                  <span class={styles.infoValue}>
                    <span class={styles.priorityBadge}>
                      {association.priority}
                    </span>
                  </span>
                </div>

                <div class={styles.infoItem}>
                  <span class={styles.infoLabel}>Color:</span>
                  <span class={styles.infoValue}>
                    {association.iconColor}
                    {association.iconColor !== "inherit" && (
                      <span
                        class={styles.colorSwatch}
                        style={{ backgroundColor: association.iconColor }}
                      />
                    )}
                  </span>
                </div>

                {association.folderIconColor && (
                  <div class={styles.infoItem}>
                    <span class={styles.infoLabel}>Folder Icon Color:</span>
                    <span class={styles.infoValue}>
                      {association.folderIconColor}
                      <span
                        class={styles.colorSwatch}
                        style={{ backgroundColor: association.folderIconColor }}
                      />
                    </span>
                  </div>
                )}

                <div class={styles.infoItem}>
                  <span class={styles.infoLabel}>Pattern:</span>
                  <span class={styles.infoValue}>
                    <code class={styles.patternCode}>
                      {association.pattern}
                    </code>
                  </span>
                </div>

                {association.fileNames && association.fileNames !== "N/A" && (
                  <div class={styles.infoItem}>
                    <span class={styles.infoLabel}>
                      {examplesLabel(icon.category)}:
                    </span>
                    <span class={`${styles.infoValue} ${styles.infoExamples}`}>
                      {firstExamples(association.fileNames)}
                    </span>
                  </div>
                )}
              </div>
            ) : (
              <div class={styles.noAssociation}>
                <p>No association data available for this icon.</p>
                <p class={styles.noAssociationHint}>
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
