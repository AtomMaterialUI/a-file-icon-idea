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
import * as puppeteer from 'puppeteer';


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

  /**
   * Generate the preview image
   * @param fileName image file name
   * @param numCols number of columns to print
   * @param assocs associations
   */
  savePreview(fileName: string, numCols: number, assocs: A[]) {
    const assocMatrix: A[][] = buildMatrix(assocs, numCols);

    const filePath = path.join(__dirname, fileName + '.html');

    // write the html file with the icon table
    fs.writeFileSync(filePath, this.createPreviewTable(assocMatrix, numCols));

    // create the image
    this.createScreenshot(filePath, fileName)
        .then(() => {
          this.logger.log(`> Successfully created ${fileName} preview image!`, this.logGroupId);
        })
        .catch(() => {
          this.logger.error(`Error while creating ${fileName} preview image`, this.logGroupId);
          throw Error();
        });
  }

  protected abstract getImagesUrl(): string;

  protected abstract generate();

  private createHTMLTable(headers: string, rows: string): string {
    return `
<table>
  ${headers}
  ${rows}
</table>
`;
  }

  /**
   * Create the headers (icon and name * numCols)
   * @param numCols number of times to repeat
   */
  private createHTMLTableHeaders(numCols: number): string {
    const pair = `
        <th class="icon">Icon</th>
        <th class="iconName">Name</th>
    `;
    const columns = [...Array(numCols)].map(() => pair).join('');
    return `
        <tr>
            ${columns}
        </tr>
    `;
  }

  /**
   * Create the rows with the assocs
   * @param assocs
   */
  private createHTMLTableBodyRows(assocs: A[][]): string {
    let rows = '';
    assocs.forEach(row => {
      const columns = row.map(icon => `
            <td class="icon">
                <img src="${this.getImagesUrl()}${icon.icon}.svg" alt="${icon.name}">
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
  private createPreviewTable(assocs: A[][], numCols: number): string {
    const table = `
<!DOCTYPE html>
<html lang="en">
  <head>
    <link rel="stylesheet" href="${path.join('style.css')}"/>
    <title></title>
  </head>
  <body>
    ${this.createHTMLTable(
        this.createHTMLTableHeaders(numCols),
        this.createHTMLTableBodyRows(assocs),
    )};
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

      await page.goto(htmlFilePath);

      await page.screenshot({
        path: `images/${fileName}.png`,
        omitBackground: true,
        fullPage: true,
      });

      await browser.close();
      return fs.readFileSync(`images/${fileName}.png`);

    } catch (error) {
      console.error(error);
      throw Error(error);
    }
  }


}