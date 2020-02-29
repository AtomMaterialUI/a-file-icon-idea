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

import * as readline from 'readline';

export interface ISpinner {
  timer: NodeJS.Timer;
  line: number;
}

export class Logger {
  private frames = ['- ', '\\ ', '| ', '/ '];
  private countLines: number = 1;

  private static cursorShow(): void {
    process.stdout.write('\u001B[?25h');
  }

  private static cursorHide(): void {
    process.stdout.write('\u001B[?25l');
  }

  /**
   * Print log header
   * @param groupId
   */
  private static getHeader(groupId: string) {
    return groupId ? `[${groupId}]: ` : '';
  }

  /**
   * Update logged line
   * @param message the message to log
   * @param line the line to replace
   */
  updateLog(message: string, line: number = 1) {
    if (!process.stdout.isTTY) {
      process.stdout.write(`${message}\n`);
      return;
    }
    // Go to the latest line
    readline.moveCursor(process.stdout, 0, -line);
    // clear that line
    readline.clearLine(process.stdout, 0);
    process.stdout.write(`${message}\n`);
    // move back to where it was
    readline.moveCursor(process.stdout, 0, line);
  }

  /**
   * Log a message
   * @param message
   * @param groupId
   */
  log(message: string, groupId?: string) {
    process.stdout.write(`${Logger.getHeader(groupId)}${message}\n`);
    this.countLines++;
  }

  /**
   * Log an error message
   * @param message
   * @param groupId
   */
  error(message: string, groupId?: string) {
    process.stderr.write(`${Logger.getHeader(groupId)}${message}\n`);
    this.countLines++;
  }

  public spinnerLogStart(message: string, groupId?: string): ISpinner {
    const line = this.countLines;
    this.log(message, groupId);
    return {timer: this.spin(message, groupId, line), line};
  }

  public spinnerLogStop(spinner: ISpinner, message?: string, groupId?: string): void {
    clearInterval(spinner.timer);
    this.updateLog(`${Logger.getHeader(groupId)}${message}`, this.countLines - spinner.line);
    Logger.cursorShow();
  }

  /**
   * Imitate a spinning cursor
   * @param message
   * @param groupId
   * @param line
   */
  private spin(message: string, groupId?: string, line?: number): NodeJS.Timer {
    if (!process.stdout.isTTY) {
      return;
    }
    let i = 0;
    return setInterval(() => {
      Logger.cursorHide();
      const frame = this.frames[i = ++i % this.frames.length];
      this.updateLog(`${Logger.getHeader(groupId)}${frame}${message}`, this.countLines - line);
    }, 80);
  }
}
