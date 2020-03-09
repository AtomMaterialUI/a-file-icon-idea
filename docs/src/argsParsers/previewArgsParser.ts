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

import yargs from 'yargs';
import {Logger} from '../services/logger';
import {ExamplesFlags} from './examplesArgsParser';
import {CommandArgs} from '../types/associations';

export enum PreviewAllowedOutputs {
  FILE = 'file',
  REPO = 'repo'
}

interface InferredArgs {
  out?: PreviewAllowedOutputs;
  token?: string;
  account?: string;
}

export interface PreviewCommandArgs extends CommandArgs {
  option?: boolean;
}

/**
 * Parse cli args
 */
export class PreviewArgsParser {
  // Allowed output types
  private readonly allowedOutputs = [PreviewAllowedOutputs.FILE, PreviewAllowedOutputs.REPO];
  // Default account to push to github
  private readonly defaultAccount = 'mallowigi';
  // Options
  private options: { [key: string]: yargs.Options } = {
    out: {
      alias: 'o',
      description: 'The output type',
      required: true,
      requiresArg: true,
      type: 'string',
    },
    account: {
      alias: 'a',
      description: 'The GitHub account to use',
      default: this.defaultAccount,
      requiresArg: true,
      type: 'string',
    },
    token: {
      alias: 't',
      description: 'The GitHub token to use for pushing commits',
      requiresArg: true,
      type: 'string',
    },
    force: {
      alias: 'f',
      type: 'boolean',
    },
  };

  /**
   * Print the yargs usage
   * @param logger
   */
  constructor(private logger: Logger) {
    yargs
        .usage('Usage: $0 <command> [options]')
        .command('all', 'Generates the preview image for files and folders')
        .command('files', 'Generates the preview image for files')
        .command('folders', 'Generates the preview image for folders')
        .demandCommand(1, 'Missing command')
        .recommendCommands()
        .options(this.options)
        .choices('out', this.allowedOutputs)
        .help()
        .version()
        .alias('h', 'help')
        .alias('v', 'version')
        .check((argv => this.validate(argv)))
        .strict();
  }

  parse(): PreviewCommandArgs {
    const pargs: yargs.Arguments<yargs.Omit<{}, keyof any> & InferredArgs> = yargs.parse(process.argv.slice(2));

    return {
      command: pargs._[0] as ExamplesFlags,
      account: pargs.account as string,
      output: pargs.out as PreviewAllowedOutputs,
      token: pargs.token as string,
      force: pargs.force as boolean,
    };
  }

  private validate(argv: yargs.Arguments<yargs.Omit<{}, keyof any> & InferredArgs>) {
    if (argv.out === PreviewAllowedOutputs.REPO && !argv.token) {
      yargs.showHelp();
      this.logger.error(`No token provided`);
      process.exit(1);
    }
    if (argv.account !== this.defaultAccount) {
      this.logger.log(`Using account: ${argv.account}`);
    }
    return true;
  }
}