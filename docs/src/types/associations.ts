export interface IconAssociation {
  name: string;
  pattern: string;
  fileNames: string;
  icon: string;

}

export interface FolderAssociation {
  name: string;
  pattern: string;
  folderNames: string;
  icon: string;
}

export interface IconAssociations {
  [name: string]: IconAssociation
}

export interface FolderAssociations {
  [name: string]: FolderAssociation
}