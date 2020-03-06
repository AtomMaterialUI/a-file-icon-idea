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

import {WikiAllowedOutputs, WikiCommandArgs} from './wikiArgsParser';
import {ISpinner, Logger} from './logger';
import {pathUnixJoin, ROOT} from './utils';
import {GitClient} from './gitClient';
import * as fs from 'fs';
import * as https from 'https';
import * as http from 'http';

export interface ListGeneratorParams {
  pargs: WikiCommandArgs,
  logger: Logger,
  gitClient: GitClient,
  logGroupId?: string,
  wikiPageFilename: string,
  associationsFile: string,
}

export abstract class ListGenerator {
  protected gitClient: GitClient;
  protected logger: Logger;
  protected pargs: WikiCommandArgs;
  protected logGroupId: string;

  /**
   * The wiki page filename
   */
  protected wikiPageFilename: string;
  /**
   * The associations file
   */
  protected associationsFile: string;

  /**
   * The url to the wiki page
   */
  protected WIKI_URL: string;

  protected constructor(params: ListGeneratorParams) {
    this.gitClient = params.gitClient;
    this.logger = params.logger;
    this.pargs = params.pargs;
    this.logGroupId = params.logGroupId;

    this.wikiPageFilename = params.wikiPageFilename;
    this.associationsFile = params.associationsFile;

    this.WIKI_URL = `https://raw.githubusercontent.com/wiki/${this.pargs.account}/${ROOT}`;
  }

  /**
   * Replace the table of associations
   * @param wikiPageContent content to replace
   */
  private static getReplaceText(wikiPageContent: string): string {
    const regex = /^\|.*\|\r\n|^\|.*\|\n/gm;
    let strToReplace = '';
    let matches = regex.exec(wikiPageContent);
    while (matches) {
      // This is necessary to avoid infinite loops with zero-width matches
      if (matches.index === regex.lastIndex) {
        regex.lastIndex++;
      }
      strToReplace += matches.join();
      // Repeat
      matches = regex.exec(wikiPageContent);
    }
    return strToReplace;
  }

  /**
   * Generate the markdown page
   */
  async generate(): Promise<{ filename: string, content: string }> {
    // First retrieve the wiki page
    const wikiPageContent = await this.getWikiPage();
    // Create the list markdown
    const createdList = this.createList();

    // Do not generate if nothing changed (repo mode)
    const hasChanged = this.compareLists(wikiPageContent, createdList);
    if (this.pargs.output === WikiAllowedOutputs.REPO && !hasChanged) {
      return;
    }

    // Create a new wiki page with the contents
    const newWikiPage = this.createNewWikiPage(wikiPageContent, createdList);
    // Write the wiki page
    this.tryWriteToFile(newWikiPage);

    return {
      filename: this.wikiPageFilename,
      content: newWikiPage,
    };
  }

  /**
   * Inserts a new line at the end
   * @param list
   * @param index
   */
  protected getLineEnd(list: any[], index: number): string {
    return index === list.length - 1 ? '|\n' : '';
  }

  /**
   * Url to images folder
   */
  protected abstract getImagesUrl();

  /**
   * Generate the list of associations in markdown
   */
  protected abstract createList(): string;

  /**
   * Get the headers text
   * @param headers list of headers
   */
  protected getHeaders(headers: string[]) {
    let mdText = '';

    headers.forEach((header, index) => {
      mdText += `| ${header} ${this.getLineEnd(headers, index)}`;
    });

    // Add the line
    for (let i = 0; i < headers.length; i++) {
      mdText += `| :---: ${this.getLineEnd(headers, i)}`;
    }

    return mdText;
  }

  /**
   * Fetch the wiki page
   */
  private async getWikiPage(): Promise<string> {
    return new Promise((resolve, reject) => {
      // If writing directly to the repo
      if (this.pargs.output === WikiAllowedOutputs.REPO) {
        try {
          // Fetch the file from wiki
          const filePath = pathUnixJoin(this.gitClient.wikiRepoFolder, this.wikiPageFilename);
          this.logger.log(`Reading wiki page from: ${filePath.replace(`${this.gitClient.rootFolder}`, '')}`, this.logGroupId);

          const src = fs.readFileSync(filePath).toString();
          return resolve(src);
        } catch (e) {
          this.logger.error(e);
          return reject(e);
        }
      }

      // Fetch wiki page from the repository
      const uri = `${this.WIKI_URL}/${this.wikiPageFilename}`;
      const spinner: ISpinner = this.logger.spinnerLogStart(`Requesting wiki page from: ${uri}`, this.logGroupId);

      // Fetch the page
      https.get(uri, (resp: http.IncomingMessage) => {
        const body = [];

        resp.on('error', err => {
          clearInterval(spinner.timer);
          reject(err.stack);
        });

        resp.on('data', chunk => {
          body.push(chunk);
        });

        resp.on('end', () => {
          this.logger.spinnerLogStop(spinner, 'Wiki page received', this.logGroupId);
          return resolve(Buffer.concat(body).toString());
        });

        if (resp.statusCode !== 200) {
          return reject(resp.statusMessage);
        }

      });
    });
  }

  /**
   * Compare lists between repo and local
   * @param wikiPageContent
   * @param createdList
   */
  private compareLists(wikiPageContent: string, createdList: string): boolean {
    // Make sure we are not in repo mode
    if (this.pargs.output !== WikiAllowedOutputs.REPO) {
      return false;
    }

    this.logger.updateLog(`Checking for changes to: '${this.associationsFile}'`);

    // Split by newlines
    const newIconsList = createdList.split(/\r\n|\n/gm);
    if (!newIconsList[newIconsList.length - 1]) {
      newIconsList.pop();
    }
    // Split wiki page by newlines
    const currentIconsList = ListGenerator.getReplaceText(wikiPageContent).split(/\r\n|\n/gm);
    if (!currentIconsList[currentIconsList.length - 1]) {
      currentIconsList.pop();
    }

    this.logger.updateLog('Comparing lists');

    const hasChanged = !newIconsList.every((value, index) => value === currentIconsList[index]);

    this.logger.updateLog(`${hasChanged ? 'C' : 'No c'}hanges detected to: '${this.associationsFile}'`);

    return hasChanged;
  }

  /**
   * Create the new wiki page
   * @param wikiPageContent
   * @param createdList
   */
  private createNewWikiPage(wikiPageContent: string, createdList: string) {
    try {
      this.logger.log('Starting new wiki page creation', this.logGroupId);
      const newWikiPage = wikiPageContent.replace(ListGenerator.getReplaceText(wikiPageContent), createdList);
      this.logger.log('New wiki page created', this.logGroupId);
      return newWikiPage;
    } catch (e) {
      throw new Error(`Failed creating new wiki page with reason: ${e}`);
    }
  }

  /**
   * Write the content to a file
   * @param content
   */
  private tryWriteToFile(content: string) {
    if (!content) {
      return;
    }

    const dirname = this.pargs.output === WikiAllowedOutputs.REPO ? this.gitClient.wikiRepoFolder : __dirname;
    const filePath = pathUnixJoin(dirname, this.wikiPageFilename);
    const filePathLog = this.pargs.output === WikiAllowedOutputs.REPO ? filePath.replace(`${this.gitClient.rootFolder}`, '') : filePath;

    this.logger.log(`Writing new wiki page to: ${filePathLog}`, this.logGroupId);
    fs.writeFileSync(filePath, content);
  }
}