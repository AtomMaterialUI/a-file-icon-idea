import fs from "fs";
import path from "path";
import { glob } from "glob";
import { XMLParser } from "fast-xml-parser";

export type Association = {
  name: string;
  iconColor: string;
  folderIconColor?: string;
  priority: number;
  pattern: string;
  fileNames: string;
  icon: string;
  url?: string;
  type: "regex" | "type";
};

export type IconCategory = {
  name: string;
  path: string;
  category: string;
  association: Association | null;
};

export type Category = "files" | "folders" | "foldersOpen" | "ui";

export type IconData = {
  all: IconCategory[];
  byCategory: Record<Category, IconCategory[]>;
  counts: Record<"all" | Category, number>;
};

const parser = new XMLParser({
  ignoreAttributes: false,
  attributeNamePrefix: "",
});

function toColor(value: unknown, fallback: string): string {
  return typeof value === "string" && value.length > 0 ? `#${value}` : fallback;
}

function iconKey(icon: string): string {
  return icon.split("/").pop()?.replace(".svg", "") ?? "";
}

// Recursively collect every node stored under `tag`, regardless of nesting depth.
function collectNodes(
  node: unknown,
  tag: string,
  out: Record<string, string>[],
): void {
  if (Array.isArray(node)) {
    for (const child of node) collectNodes(child, tag, out);
    return;
  }

  if (node === null || typeof node !== "object") return;

  for (const [key, value] of Object.entries(node as Record<string, unknown>)) {
    if (key === tag) {
      const nodes = Array.isArray(value) ? value : [value];
      for (const entry of nodes) {
        if (entry && typeof entry === "object")
          out.push(entry as Record<string, string>);
      }
    }

    collectNodes(value, tag, out);
  }
}

function parseXml(relativePath: string): unknown {
  const xmlPath = path.resolve(relativePath);
  const xmlContent = fs.readFileSync(xmlPath, "utf8");
  return parser.parse(xmlContent);
}

function getIconAssociations(): Record<string, Association> {
  const associations: Record<string, Association> = {};

  try {
    const root = parseXml(
      "../src/main/resources/iconGenerator/icon_associations.xml",
    );

    const regexNodes: Record<string, string>[] = [];
    const typeNodes: Record<string, string>[] = [];
    collectNodes(root, "regex", regexNodes);
    collectNodes(root, "type", typeNodes);

    for (const node of regexNodes) {
      const key = iconKey(node.icon ?? "");
      if (!key) continue;

      associations[key] = {
        name: node.name,
        iconColor: toColor(node.iconColor, "inherit"),
        priority: parseInt(node.priority, 10),
        pattern: node.pattern,
        fileNames: node.fileNames ?? "N/A",
        icon: node.icon,
        url: node.url ?? "",
        type: "regex",
      };
    }

    for (const node of typeNodes) {
      const key = iconKey(node.icon ?? "");
      if (!key) continue;

      associations[key] = {
        name: node.name,
        iconColor: toColor(node.iconColor, "inherit"),
        priority: parseInt(node.priority, 10),
        pattern: "Type-based association",
        fileNames: "N/A",
        icon: node.icon,
        type: "type",
      };
    }
  } catch (error) {
    console.error("Error loading icon associations:", error);
  }

  return associations;
}

function getFolderAssociations(): Record<string, Association> {
  const associations: Record<string, Association> = {};

  try {
    const root = parseXml(
      "../src/main/resources/iconGenerator/folder_associations.xml",
    );

    const regexNodes: Record<string, string>[] = [];
    collectNodes(root, "regex", regexNodes);

    for (const node of regexNodes) {
      const key = iconKey(node.icon ?? "");
      if (!key) continue;

      associations[key] = {
        name: node.name,
        iconColor: toColor(node.folderColor, "inherit"),
        folderIconColor: node.folderIconColor
          ? `#${node.folderIconColor}`
          : undefined,
        priority: parseInt(node.priority, 10),
        pattern: node.pattern,
        fileNames: node.folderNames ?? "N/A",
        icon: node.icon,
        url: node.url ?? "",
        type: "regex",
      };
    }
  } catch (error) {
    console.error("Error loading folder associations:", error);
  }

  return associations;
}

async function iconsIn(
  baseIconPath: string,
  category: Category,
  associations: Record<string, Association>,
): Promise<IconCategory[]> {
  const files = await glob("*.svg", { cwd: path.join(baseIconPath, category) });

  return files
    .map((icon) => {
      const name = icon.replace(".svg", "");
      return {
        name,
        path: `/icons/icons/${category}/${icon}`,
        category,
        association: associations[name] ?? null,
      };
    })
    .sort((a, b) => a.name.localeCompare(b.name));
}

export async function getAllIcons(): Promise<IconData> {
  const baseIconPath = path.resolve("../iconGenerator/assets/icons");
  const fileAssociations = getIconAssociations();
  const folderAssociations = getFolderAssociations();

  const empty: Record<Category, IconCategory[]> = {
    files: [],
    folders: [],
    foldersOpen: [],
    ui: [],
  };

  try {
    const [files, folders, foldersOpen, ui] = await Promise.all([
      iconsIn(baseIconPath, "files", fileAssociations),
      iconsIn(baseIconPath, "folders", folderAssociations),
      iconsIn(baseIconPath, "foldersOpen", folderAssociations),
      iconsIn(baseIconPath, "ui", fileAssociations),
    ]);

    const byCategory: Record<Category, IconCategory[]> = {
      files,
      folders,
      foldersOpen,
      ui,
    };
    const all = [...files, ...folders, ...foldersOpen, ...ui];

    return {
      all,
      byCategory,
      counts: {
        all: all.length,
        files: files.length,
        folders: folders.length,
        foldersOpen: foldersOpen.length,
        ui: ui.length,
      },
    };
  } catch (error) {
    console.error("Error loading icons:", error);
    return {
      all: [],
      byCategory: empty,
      counts: {
        all: 0,
        files: 0,
        folders: 0,
        foldersOpen: 0,
        ui: 0,
      },
    };
  }
}
