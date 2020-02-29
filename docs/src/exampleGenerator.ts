/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Chris Magnussen and Elior Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

import {Logger} from './logger';
import {CommandArgs, Flags} from './yargsParser';
import * as fs from 'fs';

interface IconAssociation {
  name: string;
  pattern: string | RegExp;
  fileNames: string;
  icon: string;

}

interface FolderAssociation {
  name: string;
  pattern: string | RegExp;
  folderNames: string;
  icon: string;
}

/**
 * Delete a directory
 * @param dirName
 */
function deleteDirectoryRecursively(dirName: string) {
  if (fs.existsSync(dirName)) {
    fs.readdirSync(dirName)
        .forEach(file => {
          const curPath = `${dirName}/${file}`;
          if (fs.lstatSync(curPath).isDirectory()) {
            deleteDirectoryRecursively(curPath);
          } else {
            fs.unlinkSync(curPath);
          }
        });
    fs.rmdirSync(dirName);
  }
}

interface IconAssociations {
  [name: string]: IconAssociation
}

interface FolderAssociations {
  [name: string]: FolderAssociation
}

export class ExampleGenerator {
  private readonly iconAssociations: IconAssociations;
  private readonly folderAssociations: FolderAssociations;
  private unsupported: Array<IconAssociation | FolderAssociation> = [];

  constructor(private pargs: CommandArgs,
              private files: IconAssociation[],
              private folders: FolderAssociation[],
              private logger: Logger) {

    this.iconAssociations = this.parseIconAssociations();
    this.folderAssociations = this.parseFolderAssociations();
  }

  /**
   * Create a directory and cd to it
   * @param dirName
   */
  private static createDirectory(dirName: string) {
    deleteDirectoryRecursively(dirName);
    fs.mkdirSync(dirName);
    process.chdir(dirName);
  }

  /**
   * Start the process
   */
  generate() {
    const currentDir = process.cwd();
    ExampleGenerator.createDirectory('examples');
    this.logger.log('');

    switch (this.pargs.flag) {
      case Flags.ALL:
        this.createFiles(Object.keys(this.iconAssociations));
        this.createFolders(Object.keys(this.folderAssociations));
        break;
      case Flags.FILES: {
        const icons = this.pargs.icons.length ? this.pargs.icons : Object.keys(this.iconAssociations);
        this.createFiles(icons);
        break;
      }
      case Flags.FOLDERS: {
        const icons = this.pargs.icons.length ? this.pargs.icons : Object.keys(this.folderAssociations);
        this.createFolders(icons);
        break;
      }
    }
    this.displayNoteFooter();
    process.chdir(currentDir);
  }

  /**
   * Parse the provided icon associations
   */
  private parseIconAssociations(): IconAssociations {
    return this.files
        .reduce((previous, current) => {
          const obj = previous;
          obj[current.name] = current;
          return obj;
        }, {});
  }

  /**
   * Parse the provided folder associations
   */
  private parseFolderAssociations(): FolderAssociations {
    return this.folders
        .reduce((previous, current) => {
          const obj = previous;
          obj[current.name] = current;
          return obj;
        }, {});
  }

  /**
   * Create files
   * @param fileNames
   */
  private createFiles(fileNames: string[]) {
    fileNames.forEach(name => {
      const iconAssociation = this.iconAssociations[name];
      if (!iconAssociation) {
        this.unsupported.push(iconAssociation);
        return;
      }

      try {
        const iconAssociations = iconAssociation.fileNames.split(',');
        iconAssociations.forEach(fileName => {
          fs.writeFileSync(fileName, null);
          this.logger.updateLog(`Example file for '${name}' successfully created!`);
        });
      } catch (e) {
        this.logger.error(`Something went wrong while creating the file(s) for '${name}' :\n${e}`);
      }
    });
  }

  /**
   * Create folders
   * @param folderNames
   */
  private createFolders(folderNames: string[]) {
    folderNames.forEach(name => {
      const folderAssociation = this.folderAssociations[name];
      if (!folderAssociation) {
        this.unsupported.push(folderAssociation);
        return;
      }

      try {
        const folderAssociations = folderAssociation.folderNames.split(',');
        folderAssociations.forEach(folder => {
          fs.mkdirSync(folder);
          this.logger.updateLog(`Example folder for '${name}' successfully created!`);
        });
      } catch (e) {
        this.logger.error(`Something went wrong while creating the file(s) for '${name}' :\n${e}`);
      }
    });
  }

  /**
   * Display a note at the footer
   */
  private displayNoteFooter() {
    const supported = this.pargs.icons.filter(icon => this.unsupported.findIndex(ass => ass.name === icon) < 0);
    let isMany = !supported.length || supported.length > 1;
    let s = isMany ? 's' : '';

    // file, folder or all
    const noun = this.pargs.flag !== Flags.ALL ?
        this.pargs.flag.substring(0, this.pargs.flag.length - 1) :
        this.pargs.flag;

    const verb = isMany ? 'were' : 'was';

    // the msg
    let msg = '';
    if (isMany) {
      msg = `${supported.join('\', \'')} ${noun}`;
    } else if (supported.length === 0) {
      msg = `zero ${noun}`;
    } else {
      msg = noun;
    }

    this.logger.updateLog(`Example${s} of ${msg} icon${s} ${verb} successfully created!`);

    if (this.unsupported.length) {
      isMany = !this.unsupported.length || this.unsupported.length > 1;
      s = isMany ? 's' : '';
      this.logger.error(`Unsupported icon${s}: '${this.unsupported.join('\', \'')}'`);
    }
  }
}
