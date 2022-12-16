import dayjs from 'dayjs';
import { CronJob } from 'cron';
import { ClickHouseClient } from '@clickhouse/client';
import { AddLinkHit, LinksRepositoryData } from '../linksRepositoryData';
import Links from '../../domain/links';
import RedirectLink from '../../domain/redirectLink';

export type LinksRepositoryConfig = {
  clickHouse: ClickHouseClient;
  cronInsertPattern: string;
  discardEventsOnInsertError: boolean;
}

export type LinkHitEntity = {
  name: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
  created_at?: string,
}

export class LinksRepository implements LinksRepositoryData {
  private config: LinksRepositoryConfig;

  private localLinkHits: LinkHitEntity[];

  constructor(config: LinksRepositoryConfig) {
    this.config = config;

    this.localLinkHits = [];

    const insertCron = new CronJob(
      this.config.cronInsertPattern,
      () => this.batchInsert(),
      null,
    );
    insertCron.start();
  }

  async batchInsert(): Promise<void> {
    if (!this.config.clickHouse) return;

    if (this.localLinkHits.length > 0) {
      try {
        await this.config.clickHouse.insert({
          table: 'link_hits',
          values: [...this.localLinkHits],
          format: 'JSONEachRow',
        });
        console.info('Batch insert of', this.localLinkHits.length, 'link hits finished');
        this.localLinkHits = [];

        return Promise.resolve();
      } catch (err) {
        console.error('Failed to insert', this.localLinkHits.length, 'link hits');
        if (this.config.discardEventsOnInsertError) {
          this.localLinkHits = [];
        }
      }
    }
  }

  async addLinkHit({
    name, meta, params,
  }: AddLinkHit): Promise<void> {
    this.localLinkHits.push({
      name,
      meta,
      params,
      created_at: dayjs().utc().format('YYYY-MM-DD HH:mm:ss'),
    });
    return Promise.resolve();
  }

  async getLinks(name: string): Promise<Links | null> {
    const response = this.config.clickHouse.query({
      query: 'SELECT * FROM redirect_links WHERE name={name: VARCHAR}',
      query_params: {
        name,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await (await response).json() as RedirectLink[];
    if (parsedResponse.length === 0) {
      return Promise.resolve(null);
    }

    return Promise.resolve({
      ios: parsedResponse[0].links.ios || '',
      android: parsedResponse[0].links.android || '',
      other: parsedResponse[0].links.other || '',
    });
  }
}
