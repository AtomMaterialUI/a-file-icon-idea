import { useMemo, useState } from "preact/hooks";
import type { IconCategory, IconData } from "../../lib/icons";
import IconCard from "./IconCard";
import IconModal from "./IconModal";
import "./icon-gallery.css";

type Filter = "all" | keyof IconData["byCategory"];

type Props = {
  icons: IconCategory[];
  counts: IconData["counts"];
};

const FILTERS: { key: Filter; label: string }[] = [
  { key: "all", label: "All" },
  { key: "files", label: "Files" },
  { key: "folders", label: "Folders" },
  { key: "foldersOpen", label: "Folders Open" },
  { key: "ui", label: "UI" },
];

export default function IconGallery({ icons, counts }: Props) {
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState<Filter>("all");
  const [selected, setSelected] = useState<IconCategory | null>(null);

  const query = search.trim().toLowerCase();

  const visible = useMemo(
    () =>
      icons.filter((icon) => {
        const categoryMatch = filter === "all" || icon.category === filter;
        const searchMatch = query === "" || icon.name.toLowerCase().includes(query);
        return categoryMatch && searchMatch;
      }),
    [icons, filter, query],
  );

  return (
    <div class="icon-gallery">
      <div class="search-section">
        <input
          type="text"
          class="search-input"
          placeholder="Search icons by name or association..."
          value={search}
          onInput={(event) => setSearch((event.target as HTMLInputElement).value)}
        />

        <div class="filter-buttons">
          {FILTERS.map(({ key, label }) => (
            <button
              type="button"
              key={key}
              class={`filter-btn${filter === key ? " active" : ""}`}
              onClick={() => setFilter(key)}
            >
              {label} ({counts[key]})
            </button>
          ))}
        </div>

        <div class="stats">
          <span>{visible.length}</span> icons shown
        </div>
      </div>

      {visible.length > 0 ? (
        <div class="icon-grid">
          {visible.map((icon) => (
            <IconCard key={`${icon.category}/${icon.name}`} icon={icon} onSelect={setSelected} />
          ))}
        </div>
      ) : (
        <div class="no-results">
          <h3>No icons found</h3>
          <p>Try adjusting your search terms or filters</p>
        </div>
      )}

      <IconModal icon={selected} onClose={() => setSelected(null)} />
    </div>
  );
}
