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
import * as fs from 'fs';
import {Clone, Cred, PushOptions, Reference, Remote, Repository, Signature} from 'nodegit';
import {findDirectorySync, pathUnixJoin, ROOT} from './utils';
import {clearInterval} from 'timers';

export class GitClient {
  /**
   * Path to the wiki
   */
  wikiRepoFolder: string;
  /**
   * Path to the folder containing the icons
   */
  rootFolder: string | RegExp;
  /**
   * A groupId to group logs into
   */
  private logGroupId: string = 'git';
  /**
   * Path to the code repository
   */
  private readonly codeRepoFolder: string;
  /**
   * Url to the code repo
   */
  private readonly codeRepoUrl: string;
  /**
   * Url to the wiki
   */
  private readonly wikiRepoUrl: string;

  /**
   * Code Repository
   */
  private codeRepo: Repository;
  /**
   * Wiki Repository
   */
  private wikiRepo: Repository;

  constructor(private pargs: WikiCommandArgs, private logger: Logger) {
    this.rootFolder = pathUnixJoin(findDirectorySync(ROOT), './../../');
    this.codeRepoFolder = pathUnixJoin(this.rootFolder, this.pargs.account, ROOT);
    this.wikiRepoFolder = pathUnixJoin(this.rootFolder, this.pargs.account, `${ROOT}.wiki`);

    this.codeRepoUrl = `https://github.com/${this.pargs.account}/${ROOT}`;
    this.wikiRepoUrl = `${this.codeRepoUrl}.wiki`;
  }

  /**
   * Create the origin remote
   * @param repo
   * @param url
   */
  private static addRemote(repo: Repository, url: string) {
    return Remote.create(repo, 'origin', url);
  }

  /**
   * Checks if there are changes in the provided filename
   * @param repo the repo to check
   * @param filename the filename to check
   */
  private static async checkForDiff(repo: Repository, filename: string): Promise<boolean> {
    const commit = await repo.getMasterCommit();

    for (const diff of await commit.getDiff()) {
      for (const patch of await diff.patches()) {
        const exists = new RegExp(`.*/${filename}$`, 'gi').test(patch.newFile().path());
        if (exists) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Get the code repository
   */
  async getCodeRepository() {
    if (this.pargs.output !== WikiAllowedOutputs.REPO) {
      return;
    }

    this.codeRepo = await this.getRepository(this.codeRepoUrl, this.codeRepoFolder);
  }

  /**
   * Get the wiki repository
   */
  async getWikiRepository() {
    if (this.pargs.output !== WikiAllowedOutputs.REPO) {
      return;
    }

    this.wikiRepo = await this.getRepository(this.wikiRepoUrl, this.wikiRepoFolder);
  }

  /**
   * Try to commit to the wiki repo
   * @param filename
   * @param content
   */
  async tryCommitToWikiRepo(filename: string, content: any): Promise<boolean> {
    if (this.pargs.output !== WikiAllowedOutputs.REPO || !content) {
      return;
    }
    if (!this.wikiRepo) {
      await this.getWikiRepository();
    }

    return this.commit(this.wikiRepo, filename);
  }

  /**
   * Try to push to the wiki repo
   * @param numOfCommits
   */
  async tryPushToWikiRepo(numOfCommits: number) {
    if (this.pargs.output !== WikiAllowedOutputs.REPO) {
      return;
    }
    if (!this.wikiRepo) {
      await this.getWikiRepository();
    }

    // Fetch or add remote
    let remote = await this.wikiRepo.getRemote('origin');
    if (!remote) {
      remote = GitClient.addRemote(this.wikiRepo, this.wikiRepoUrl);
    }
    await this.push(remote, numOfCommits);
  }

  /**
   * Check if the filename has changed in the repo
   * @param filename
   */
  public async checkFileChanged(filename: string): Promise<boolean> {
    if (this.pargs.output !== WikiAllowedOutputs.REPO) {
      return;
    }
    if (!this.codeRepo) {
      await this.getCodeRepository();
    }

    return GitClient.checkForDiff(this.codeRepo, filename);
  }

  /**
   * Clone or open the git repository
   * @param url
   * @param folder
   */
  private async getRepository(url: string, folder: string): Promise<Repository> {
    if (url && !fs.existsSync(folder)) {
      return this.cloneRepo(url, folder);
    } else {
      return Repository.open(folder);
    }
  }

  /**
   * Clone a repository into a folder
   * @param url url repository
   * @param folder foler
   */
  private async cloneRepo(url: string, folder: string): Promise<Repository> {
    const message = `Cloning repo: '${url}' into '${folder.replace(`${this.rootFolder}`, '')}'`;
    const spinner: ISpinner = this.logger.spinnerLogStart(message, this.logGroupId);
    try {
      const clone = await Clone.clone(url, folder);
      this.logger.spinnerLogStop(spinner, message.replace('Cloning', 'Cloned'), this.logGroupId);

      return clone;
    } catch (e) {
      clearInterval(spinner.timer);
      throw e;
    }

  }

  /**
   * Commit a filename to the repository
   * @param repo
   * @param filename
   */
  private async commit(repo: Repository, filename: string): Promise<boolean> {
    const spinner: ISpinner = this.logger.spinnerLogStart(`Creating commit`, this.logGroupId);

    try {
      // Refresh all indexes
      const index = await repo.refreshIndex();

      // git add
      await index.addByPath(filename);
      if (!index.write()) {
        throw new Error('Failed writing repo index');
      }

      const matches = filename.match(/associations|folder_associations/i);
      const name = matches && matches[0];
      if (!name) {
        throw new Error('Can not determine list name');
      }

      const commitMessage = `:robot: Update list of ${name.toLowerCase()}`;
      const time = +(Date.now() / 1000).toFixed(0); // unix UTC
      const author = Signature.create('hayate', 'hayate@github.com', time, 0); // our own bot!!
      const committer = author;
      // Get the commit message
      const oid = await index.writeTree();
      // Get the head ID
      const headId = await Reference.nameToId(repo, 'HEAD');

      // Try to create commit
      await repo.createCommit('HEAD', author, committer, commitMessage, oid, [headId]);

      this.logger.spinnerLogStop(spinner, `Commit created: ${headId.tostrS()}`, this.logGroupId);
      return true;
    } catch (e) {
      clearInterval(spinner.timer);
      throw e;
    }
  }

  /**
   * Push the master branch
   * @param remote
   * @param numOfCommits
   */
  private async push(remote: Remote, numOfCommits: number) {
    const options: PushOptions = {
      callbacks: {
        credentials: () => Cred.userpassPlaintextNew(this.pargs.account, this.pargs.token),
      },
    };
    const s = numOfCommits > 1 ? 's' : '';
    const spinner: ISpinner = this.logger.spinnerLogStart(`Pushing commit${s} to: ${remote.url()}`, this.logGroupId);
    // Interrupt the push after one minute
    const timer = setTimeout(() => {
      clearInterval(spinner.timer);
      throw new Error('Timeout on push action');
    }, 60000);

    try {
      // Push master
      const result = await remote.push(['refs/heads/master:refs/heads/master'], options);
      this.logger.spinnerLogStop(spinner, `Commit${s} pushed`, this.logGroupId);
      clearTimeout(timer);
      return result;
    } catch (e) {
      clearInterval(spinner.timer);
      throw e;
    }
  }

}