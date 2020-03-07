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
import {WikiCommandArgs} from './wikiArgsParser';
import {ExamplesFlags} from './examplesArgsParser';
import {FilesListGenerator} from './filesListGenerator';
import {FoldersListGenerator} from './foldersListGenerator';
import {FolderAssociation, IconAssociation} from './associations';
import {GitClient} from './gitClient';

export class WikiGenerator {
  filesListGenerator: FilesListGenerator;
  foldersListGenerator: FoldersListGenerator;

  constructor(private pargs: WikiCommandArgs,
              files: IconAssociation[],
              folders: FolderAssociation[],
              private logger: Logger,
              private gitClient: GitClient) {
    this.filesListGenerator = new FilesListGenerator({
      pargs,
      files,
      associationsFile: '',
      wikiPageFilename: '',
      logger,
      gitClient,
    });

    this.foldersListGenerator = new FoldersListGenerator({
      associationsFile: '',
      wikiPageFilename: '',
      pargs,
      folders,
      logger,
      gitClient,
    });

  }

  /**
   * Start the process
   */
  async generate() {
    const results = [];
    this.logger.log(`Running generate command for ${this.pargs.command}`, 'wiki');
    switch (this.pargs.command) {
      case ExamplesFlags.ALL:
        results.push(await this.filesListGenerator.generate());
        results.push(await this.foldersListGenerator.generate());
        break;
      case ExamplesFlags.FILES:
        results.push(await this.filesListGenerator.generate());
        break;
      case ExamplesFlags.FOLDERS:
        results.push(await this.foldersListGenerator.generate());
        break;
    }

    try {
      let hasCommit: boolean;
      if (results && results.length) {
        for (const result of results) {
          hasCommit = await this.gitClient.tryCommitToWikiRepo(result.filename, result.content) || hasCommit;
        }
      }

      if (hasCommit) {
        await this.gitClient.tryPushToWikiRepo(results.length);
      }

      this.logger.log('Finished');
    } catch (e) {
      const error = e instanceof Error ? e : new Error(e);
      this.logger.error(error.stack);
      process.exit(1);
    }
  }
}