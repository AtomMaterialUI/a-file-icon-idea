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

import {BasePreviewGenerator, PreviewGeneratorParams} from './basePreviewGenerator';
import {IconAssociation} from './types/associations';
import {ROOT} from './utils';

export interface FilesPreviewGeneratorParams extends PreviewGeneratorParams {
  files: IconAssociation[],
}

export class FilesPreviewGenerator extends BasePreviewGenerator<IconAssociation> {
  private readonly files: IconAssociation[];

  constructor(params: FilesPreviewGeneratorParams) {
    super({
      fileName: 'fileIcons.png',
      logGroupId: 'files',
      pargs: params.pargs,
      logger: params.logger,
      gitClient: params.gitClient,
    });
    this.files = params.files;
  }

  async generate(): Promise<{ filename: string, content: any }> {
    return {
      filename: 'fileIcons.png',
      content: this.savePreview('files', 5, this.files),
    };
  }

  protected getImagesUrl() {
    return `https://raw.githubusercontent.com/${this.pargs.account}/${ROOT}/master/src/main/resources`;
  }
}