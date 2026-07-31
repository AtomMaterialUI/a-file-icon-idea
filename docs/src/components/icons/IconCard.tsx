import type { IconCategory } from "../../lib/icons";

type Props = {
  icon: IconCategory;
  onSelect: (icon: IconCategory) => void;
};

export default function IconCard({ icon, onSelect }: Props) {
  return (
    <button type="button" class="icon-item" onClick={() => onSelect(icon)}>
      <div class="icon-display">
        <img
          src={icon.path}
          alt={icon.name}
          width="24"
          height="24"
          loading="lazy"
          decoding="async"
        />
      </div>
      <div class="icon-name">{icon.name}</div>
      <div class="icon-category">{icon.category}</div>
    </button>
  );
}
