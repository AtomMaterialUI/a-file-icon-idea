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
import yargs from 'yargs';

interface InferredArgs {
  all?: { [key: string]: yargs.Options };
  files?: { [key: string]: yargs.Options };
  folders?: { [key: string]: yargs.Options };
}

export interface CommandArgs {
  flag: string;
  icons: string[];
}

/**
 * Parse cli args
 */
export class YargsParser {
  private options: any = {
    all: {
      description: 'Generates examples for all icons',
      type: 'boolean',
    },
    files: {
      description: 'Generates examples for all file icons',
      type: 'boolean',
    },
    folders: {
      description: 'Generates examples for all folder icons',
      type: 'boolean',
    },
  };
  private supportedFlags: string[] = ['--all', '--files', '--folders'];
  private readonly optionsKeys: string[];

  constructor(private logger: Logger) {
    this.optionsKeys = Object.keys(this.options);

    yargs
        .usage('Usage: $0 <flag> [space separated icon names]')
        .options(this.options)
        .group(this.optionsKeys, 'Options')
        .alias('h', 'help')
        .alias('v', 'version')
        .epilogue(`Providing the icon names after '--files' or '--folders', restricts the examples generator to that icons only`)
        .check((argv => this.validate(argv)))
        .strict();
  }

  /**
   * Returns the arguments
   */
  parse(): CommandArgs {
    const pargs = yargs.argv;
    return {
      flag: this.getFlag(pargs),
      icons: pargs._, // the rest of the args
    };
  }

  /**
   *   Validate that all args are here and valid
   */
  private validate(argv: yargs.Arguments<yargs.Omit<{}, keyof any> & InferredArgs>) {
    if (!argv.all && !argv.files && !argv.folders) {
      this.errorHandler('Missing flag argument');
    }
    if (this.supportedFlags.indexOf(process.argv[2]) === -1) {
      this.errorHandler('Incorrect flag position');
    }

    return true;
  }

  /**
   * Get the requested flag - first all, then files or folders
   * @param argv
   */
  private getFlag(argv: yargs.Arguments<yargs.Omit<{}, keyof any> & InferredArgs>): string {
    if (argv.all) return this.optionsKeys[0];
    if (argv.files) return this.optionsKeys[1];
    if (argv.folders) return this.optionsKeys[2];
    this.errorHandler('Invalid flag');
  }

  /**
   * Show error message
   * @param message
   */
  private errorHandler(message: string) {
    yargs.showHelp();
    this.logger.error(message);
    process.exit(1);
  }
}
