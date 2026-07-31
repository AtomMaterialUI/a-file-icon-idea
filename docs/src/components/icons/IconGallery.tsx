import { useEffect, useMemo, useRef, useState } from "preact/hooks";
import { flushSync } from "preact/compat";
import type { IconCategory, IconData } from "../../lib/icons";
import IconCard from "./IconCard";
import IconModal from "./IconModal";
import "./icon-gallery.css";

type Filter = "all" | keyof IconData["byCategory"];

type Props = {
  icons: IconCategory[];
  counts: IconData["counts"];
};

const PAGE_SIZE = 80;

const FILTERS: { key: Filter; label: string }[] = [
  { key: "all", label: "All" },
  { key: "files", label: "Files" },
  { key: "folders", label: "Folders" },
  { key: "foldersOpen", label: "Folders Open" },
  { key: "ui", label: "UI" },
];

function withTransition(update: () => void) {
  if (typeof document !== "undefined" && "startViewTransition" in document) {
    document.startViewTransition(() => flushSync(update));
  } else {
    update();
  }
}

export default function IconGallery({ icons, counts }: Props) {
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState<Filter>("all");
  const [selected, setSelected] = useState<IconCategory | null>(null);
  const [limit, setLimit] = useState(PAGE_SIZE);

  const sentinelRef = useRef<HTMLDivElement>(null);

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

  const shown = visible.slice(0, limit);
  const hasMore = visible.length > limit;

  // Reveal more rows as the sentinel nears the viewport. Re-observing on each
  // change keeps loading until the sentinel is pushed off-screen.
  useEffect(() => {
    if (!hasMore) return;

    const sentinel = sentinelRef.current;
    if (!sentinel) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((entry) => entry.isIntersecting)) {
          setLimit((current) => current + PAGE_SIZE);
        }
      },
      { rootMargin: "600px" },
    );

    observer.observe(sentinel);
    return () => observer.disconnect();
  }, [hasMore, visible, limit]);

  const changeFilter = (key: Filter) =>
    withTransition(() => {
      setFilter(key);
      setLimit(PAGE_SIZE);
    });

  const changeSearch = (value: string) =>
    withTransition(() => {
      setSearch(value);
      setLimit(PAGE_SIZE);
    });

  return (
    <div class="icon-gallery">
      <div class="search-section">
        <input
          type="text"
          class="search-input"
          placeholder="Search icons by name or association..."
          value={search}
          onInput={(event) => changeSearch((event.target as HTMLInputElement).value)}
        />

        <div class="filter-buttons">
          {FILTERS.map(({ key, label }) => (
            <button
              type="button"
              key={key}
              class={`filter-btn${filter === key ? " active" : ""}`}
              onClick={() => changeFilter(key)}
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
        <>
          <div class="icon-grid">
            {shown.map((icon) => (
              <IconCard key={`${icon.category}/${icon.name}`} icon={icon} onSelect={setSelected} />
            ))}
          </div>
          {hasMore && <div ref={sentinelRef} class="scroll-sentinel" aria-hidden="true" />}
        </>
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
