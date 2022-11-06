import 'dotenv';
import { ClickHouseClient } from '@clickhouse/client';
import path from 'path';
import fs from 'fs';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { CronJob } from 'cron';
import {
  AddEvent, CountOnline, DataStorage, SearchForEvents, Timespan,
} from '../dataStorage';
import { EventEntity } from './entities';
import { Event } from '../../domain/event';
import { Pathname } from '../../domain/pathname';
import { Type } from '../../domain/type';

dayjs.extend(utc);

export class ClickHouseStorage implements DataStorage {
  private readonly clickHouse: ClickHouseClient;

  private insertCron: CronJob;

  private backupCron: CronJob;

  private localEvents: EventEntity[];

  constructor(clickHouse: ClickHouseClient) {
    this.clickHouse = clickHouse;
    this.localEvents = [];
    this.insertCron = new CronJob(process.env.CRON_INSERT_PATTERN || '* * * * * *', () => this.batchInsert(), null);
    this.insertCron.start();

    if (process.env.BACKUP_S3_ENABLE === '1') {
      this.backupCron = new CronJob(process.env.BACKUP_CRON_PATTERN || '0 0 */1 * *', () => this.backup(), null);
      this.backupCron.start();
      const nextBackupDates = this.backupCron.nextDates();
      console.info('Next backup run at', nextBackupDates.toFormat('yyyy-MM-dd HH:mm:ss'));
    }
  }

  async batchInsert(): Promise<void> {
    if (this.clickHouse !== undefined && this.localEvents.length > 0) {
      this.clickHouse.insert({
        table: 'events',
        values: [...this.localEvents],
        format: 'JSONEachRow',
      }).then(() => {
        console.info('Batch insert of', this.localEvents.length, 'events finished');
        this.localEvents = [];
        return Promise.resolve();
      }).catch((err) => {
        console.error('Failed to insert', this.localEvents.length, 'events');
        if (process.env.DISCARD_EVENTS_ON_INSERT_ERROR === '1') {
          this.localEvents = [];
        }
        return Promise.reject(err);
      });
    }
  }

  async backup(): Promise<void> {
    const backupStartTime = performance.now();
    console.info('Backup to S3 started');
    this.clickHouse.exec({
      query: `BACKUP DATABASE ${process.env.CLICKHOUSE_NAME} TO S3('${process.env.BACKUP_S3_URL}/${Date.now()}', '${process.env.BACKUP_S3_ACCESS_KEY}', '${process.env.BACKUP_S3_SECRET_KEY}');`,
    }).then(() => {
      const backupFinishTime = performance.now();
      console.info('Backup to S3 finished in', backupFinishTime - backupStartTime, 'ms');
    }).catch((err) => {
      console.error('Backup to S3 failed', err);
    });
  }

  async migrate(dir: string): Promise<void> {
    const files = fs.readdirSync(dir);
    files.sort((a, b) => Number(a.split('_')[0]) - Number(b.split('_')[0]));
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const migrationFile = path.join(dir, file);

      console.info(`Migrating ${migrationFile}`);
      const migrationFileContent = fs.readFileSync(migrationFile, { encoding: 'utf8' });
      await this.clickHouse.exec({
        query: migrationFileContent,
      });
    }
  }

  async addEvent({
    type, params, meta, fingerprint, pathname,
  }: AddEvent): Promise<void> {
    this.localEvents.push({
      pathname,
      fingerprint,
      type,
      meta,
      params,
      created_at: dayjs().utc().format('YYYY-MM-DD HH:mm:ss'),
    });
    return Promise.resolve();
  }

  async getEvents({
    pathname, type, fingerprint, dateFrom, dateTo, limit,
  }: SearchForEvents): Promise<Event[]> {
    let query = 'SELECT * FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    if (pathname) {
      query += ' AND pathname = {pathname: VARCHAR}';
    }

    if (type) {
      query += ' AND type = {type: VARCHAR}';
    }

    if (fingerprint) {
      query += ' AND fingerprint = {fingerprint: VARCHAR}';
    }

    query += ' ORDER BY created_at DESC LIMIT {limit: INT}';

    const response = await this.clickHouse.query({
      query,
      query_params: {
        dateFrom: dayjs(dateFrom).utc().format('YYYY-MM-DD HH:mm:ss'),
        dateTo: dateTo ? dayjs(dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        typeParam: type || '',
        pathname: pathname || '',
        limit: limit || 100,
        fingerprint: fingerprint || '',
      },
      format: 'JSONEachRow',
    });

    const events = await response.json() as Event[];
    console.info('Found', events.length, 'events');
    return Promise.resolve(events);
  }

  async countOnline({ pathname }: CountOnline): Promise<number> {
    const onlineTimespan = Number(process.env.ONLINE_TIMESPAN || 5);

    let query = 'SELECT COUNT(DISTINCT fingerprint) as online FROM events WHERE (created_at > now() - INTERVAL {onlineTimespan: INT} MINUTE)';

    if (pathname) {
      query += ' AND pathname={pathname: VARCHAR}';
    }

    const response = await this.clickHouse.query({
      query,
      query_params: {
        onlineTimespan,
        pathname: pathname || '',
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as {online: number}[];
    const online = Number(parsedResponse[0].online);
    console.info('Found', online, 'online users in the last', onlineTimespan, 'minutes');
    return Promise.resolve(online);
  }

  async getPathnames({ dateTo, dateFrom }: Timespan): Promise<Pathname[]> {
    let query = 'SELECT pathname, COUNT(*) count FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    query += ' GROUP BY pathname ORDER BY count DESC';

    const response = await this.clickHouse.query({
      query,
      query_params: {
        dateTo: dateTo ? dayjs(dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as Pathname[];
    return Promise.resolve(parsedResponse);
  }

  async getTypes({ dateTo, dateFrom }: Timespan): Promise<Type[]> {
    let query = 'SELECT type, COUNT(*) count FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    query += ' GROUP BY type ORDER BY count DESC';

    const response = await this.clickHouse.query({
      query,
      query_params: {
        dateTo: dateTo ? dayjs(dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as Type[];
    return Promise.resolve(parsedResponse);
  }
}
