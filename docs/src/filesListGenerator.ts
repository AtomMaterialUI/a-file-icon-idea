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
import {IconAssociation} from './associations';
import {ROOT, slugify} from './utils';

export interface FilesListGeneratorParams extends ListGeneratorParams {
  files: IconAssociation[],
}

export class FilesListGenerator extends ListGenerator {
  private files: IconAssociation[];

  constructor(param: FilesListGeneratorParams) {
    super({
      wikiPageFilename: 'associations.md',
      associationsFile: 'icon_associations.json',
      logGroupId: 'files',
      pargs: param.pargs,
      logger: param.logger,
      gitClient: param.gitClient,
    });
    this.files = param.files;
  }

  protected createList(): string {
    const listHeaders = [
      'Name',
      'Pattern',
      'Examples',
      'Icon',
    ];

    let mdText = '';
    this.logger.log('Starting creating icon associations', this.logGroupId);

    // Headers and separator
    mdText += this.getHeaders(listHeaders);

    // Add lines
    this.files.forEach(iconAssociation => {
      mdText += this.getName(iconAssociation);
      mdText += this.getPattern(iconAssociation);
      mdText += this.getExamples(iconAssociation);
      mdText += this.getIcon(iconAssociation);
      mdText += this.getLineEnd([], -1);
    });

    this.logger.log('Finished creating icon associations', this.logGroupId);

    return mdText;
  }

  protected getImagesUrl() {
    return `https://github.com/${this.pargs.account}/${ROOT}/blob/master/src/main/resources/icons/files/`;
  }

  private getName(iconAssociation: IconAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` [${iconAssociation.name}](#${slugify(iconAssociation.name)}) `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }

  private getPattern(iconAssociation: IconAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` <code>${iconAssociation.pattern}</code> `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }

  private getExamples(iconAssociation: IconAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` ${iconAssociation.fileNames} `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }

  private getIcon(iconAssociation: IconAssociation) {
    let mdText = '| ';

    mdText += this.pargs.useSmallFonts ? '<sub>' : '';
    mdText += ` ![${iconAssociation.icon}](${this.getImagesUrl()}${iconAssociation.icon}) `;
    mdText += this.pargs.useSmallFonts ? '</sub>' : '';
    return mdText;
  }
}