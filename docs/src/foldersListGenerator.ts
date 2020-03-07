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

import {ListGenerator, ListGeneratorParams} from './listGenerator';
import {FolderAssociation} from './associations';
import {ROOT, slugify} from './utils';

export interface FoldersListGeneratorParams extends ListGeneratorParams {
  folders: FolderAssociation[],
}

export class FoldersListGenerator extends ListGenerator {
  private folders: FolderAssociation[];

  constructor(param: FoldersListGeneratorParams) {
    super({
      wikiPageFilename: 'folder_associations.md',
      associationsFile: 'folder_associations.json',
      logGroupId: 'folders',
      pargs: param.pargs,
      logger: param.logger,
      gitClient: param.gitClient,
    });
    this.folders = param.folders;
  }

  protected getImagesUrl() {
    return `https://raw.githubusercontent.com/${this.pargs.account}/${ROOT}/master/src/main/resources/icons/`;
  }

  protected createList(): string {
    const listHeaders = [
      'Name',
      'Pattern',
      'Examples',
      'Closed Icon',
      'Opened Icon',
    ];

    let mdText = '';
    this.logger.log('Starting creating folder associations', this.logGroupId);

    // Headers and separator
    mdText += this.getHeaders(listHeaders);

    // Add lines
    this.folders.forEach(folderAssociation => {
      mdText += this.getName(folderAssociation);
      mdText += this.getPattern(folderAssociation);
      mdText += this.getExamples(folderAssociation);
      mdText += this.getClosedIcon(folderAssociation);
      mdText += this.getOpenedIcon(folderAssociation);
      mdText += this.getLineEnd([], -1);
    });

    this.logger.log('Finished creating folder associations', this.logGroupId);

    return mdText;
  }

  private getName(folderAssociation: FolderAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` [${folderAssociation.name}](#${slugify(folderAssociation.name)}) `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }

  private getPattern(folderAssociation: FolderAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` \`${folderAssociation.pattern} \` `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }

  private getExamples(folderAssociation: FolderAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` ${folderAssociation.folderNames.split(',').join('<br>')} `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }

  private getClosedIcon(folderAssociation: FolderAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` ![${folderAssociation.name}](${this.getImagesUrl()}folders/${folderAssociation.icon}?sanitize=true) `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }

  private getOpenedIcon(folderAssociation: FolderAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` ![${folderAssociation.name}](${this.getImagesUrl()}foldersOpen/${folderAssociation.icon}?sanitize=true) `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }
}