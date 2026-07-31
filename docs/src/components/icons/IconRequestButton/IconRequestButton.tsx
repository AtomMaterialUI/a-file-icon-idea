import { useState } from "preact/hooks";
import IconRequestModal from "../IconRequestModal";
import styles from "./IconRequestButton.module.css";

export default function IconRequestButton() {
  const [open, setOpen] = useState(false);

  return (
    <>
      <button
        type="button"
        class={styles.requestBtn}
        onClick={() => setOpen(true)}
      >
        Icon Request
      </button>

      <IconRequestModal open={open} onClose={() => setOpen(false)} />
    </>
  );
}
