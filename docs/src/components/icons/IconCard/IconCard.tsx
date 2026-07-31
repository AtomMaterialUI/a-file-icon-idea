import type { IconCategory } from "../../../lib/icons";
import styles from "./IconCard.module.css";

type Props = {
  icon: IconCategory;
  onSelect: (icon: IconCategory) => void;
};

export default function IconCard({ icon, onSelect }: Props) {
  return (
    <button type="button" class={styles.iconItem} onClick={() => onSelect(icon)}>
      <div class={styles.iconDisplay}>
        <img
          src={icon.path}
          alt={icon.name}
          width="24"
          height="24"
          loading="lazy"
          decoding="async"
        />
      </div>
      <div class={styles.iconName}>{icon.name}</div>
      <div class={styles.iconCategory}>{icon.category}</div>
    </button>
  );
}
