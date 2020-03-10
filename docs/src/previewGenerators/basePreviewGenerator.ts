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

import {Association} from '../types/associations';
import {GitClient} from '../services/gitClient';
import {PreviewCommandArgs} from '../argsParsers/previewArgsParser';
import {Logger} from '../services/logger';
import {buildMatrix} from '../utils';
import path from 'path';
import fs from 'fs';
import puppeteer from 'puppeteer';


export interface PreviewGeneratorParams {
  pargs: PreviewCommandArgs,
  logger: Logger,
  gitClient: GitClient<PreviewCommandArgs>,
  logGroupId?: string,
  fileName: string,
}

export abstract class BasePreviewGenerator<A extends Association> {
  protected gitClient: GitClient<PreviewCommandArgs>;
  protected logger: Logger;
  protected pargs: PreviewCommandArgs;
  protected logGroupId: string;

  /**
   * The image fileName
   */
  protected fileName: string;

  protected constructor(params: PreviewGeneratorParams) {
    this.gitClient = params.gitClient;
    this.logger = params.logger;
    this.pargs = params.pargs;
    this.logGroupId = params.logGroupId;

    this.fileName = params.fileName;
  }

  private static createHTMLTable(rows: string): string {
    return `
<table>
  ${rows}
</table>
`;
  }

  /**
   * Generate the preview image
   * @param fileName image file name
   * @param numCols number of columns to print
   * @param assocs associations
   */
  async savePreview(fileName: string, numCols: number, assocs: A[]) {
    const assocMatrix: A[][] = buildMatrix(assocs, numCols);

    const filePath = path.join(__dirname, fileName + '.html');

    try {
      // write the html file with the icon table
      fs.writeFileSync(filePath, this.createPreviewTable(assocMatrix));

      // create the image
      await this.createScreenshot(filePath, fileName);
      this.logger.log(`> Successfully created ${fileName} preview image!`, this.logGroupId);
    } catch {
      this.logger.error(`Error while creating ${fileName} preview image`, this.logGroupId);
    }
  }

  protected abstract getImagesUrl(): string;

  protected abstract generate();

  /**
   * Create the rows with the assocs
   * @param assocs
   */
  private createHTMLTableBodyRows(assocs: A[][]): string {
    let rows = '';
    assocs.forEach(row => {
      const columns = row.map(icon => `
            <td class="icon">
                <img src="${this.getImagesUrl()}${icon.icon}?sanitize=true" alt="${icon.name}">
            </td>
            <td class="iconName">${icon.name}</td>
        `).join('');

      const tableRow = `
            <tr>
                ${columns}
            </tr>
        `;
      rows = rows + tableRow;
    });
    return rows;
  }

  /**
   * Generate html string for the preview table of the icons
   * @param assocs icon associations
   * @param numCols number of cols
   */
  private createPreviewTable(assocs: A[][]): string {
    const table = `
<!DOCTYPE html>
<html lang="en">
  <head>
    <link rel="stylesheet" href="${path.join('../../assets/style.css')}"/>
    <title></title>
  </head>
  <body>
    ${BasePreviewGenerator.createHTMLTable(this.createHTMLTableBodyRows(assocs))}
  </body>
</html>
`;
    return table;
  }

  /**
   * Create a screenshot using puppeter
   * @param filePath
   * @param fileName
   */
  private async createScreenshot(filePath: string, fileName: string) {
    try {
      this.logger.log('Creating a screenshot using puppeter', this.logGroupId);

      const htmlFilePath = path.join('file:', filePath);
      const browser = await puppeteer.launch();
      const page = await browser.newPage();
      await page.setViewport({
        height: 10,
        width: 1000,
      });

      await page.goto(htmlFilePath, {
        timeout: 3_000_000,
      });

      await page.screenshot({
        path: `${fileName}.png`,
        omitBackground: true,
        fullPage: true,
      });

      await browser.close();
      return fs.readFileSync(`${fileName}.png`);

    } catch (error) {
      console.error(error);
      throw Error(error);
    }
  }


}