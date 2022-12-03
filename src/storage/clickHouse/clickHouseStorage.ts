import 'dotenv';
import { ClickHouseClient } from '@clickhouse/client';
import path from 'path';
import fs from 'fs';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { CronJob } from 'cron';
import {
  AddEvent,
  AddSession,
  AddUser,
  CountOnline,
  DataStorage,
  GetUserByEmail,
  SearchForEvents,
  Timespan,
} from '../dataStorage';
import { EventEntity } from './entities';
import { Event } from '../../domain/event';
import { Pathname } from '../../domain/pathname';
import { Type } from '../../domain/type';
import { User } from '../../domain/user';

dayjs.extend(utc);

export type ClickHouseStorageConfig = {
  clickHouse: ClickHouseClient,
  cronInsertPattern: string,
  discardEventsOnInsertError: boolean,
  onlineTimespan: number,
  backupToS3: {
    databaseToBackup: string,
    enable: boolean,
    cronPattern: string,
    s3Url: string,
    secretKey: string,
    accessKey: string,
  },
}

export class ClickHouseStorage implements DataStorage {
  private storageConfig: ClickHouseStorageConfig;

  private localEvents: EventEntity[];

  constructor(storageConfig: ClickHouseStorageConfig) {
    this.storageConfig = storageConfig;
    this.localEvents = [];
    const insertCron = new CronJob(
      this.storageConfig.cronInsertPattern,
      () => this.batchInsert(),
      null,
    );
    insertCron.start();

    if (this.storageConfig.backupToS3.enable) {
      const backupCron = new CronJob(
        this.storageConfig.backupToS3.cronPattern,
        () => this.backup(),
        null,
      );
      backupCron.start();
      const nextBackupDates = backupCron.nextDates();
      console.info('Next backup run at', nextBackupDates.toFormat('yyyy-MM-dd HH:mm:ss'));
    }
  }

  async addSession(data: AddSession): Promise<void> {
    try {
      await this.storageConfig.clickHouse.insert({
        table: 'sessions',
        values: [{
          user_id: data.userId,
          expires_at: dayjs(data.expiresAt).utc().format('YYYY-MM-DD HH:mm:ss'),
          refresh_token: data.refreshToken,
          ip: data.ip,
        }],
        format: 'JSONEachRow',
      });

      return Promise.resolve();
    } catch (err) {
      console.error('Inserting session failed', err);
      return Promise.reject(err);
    }
  }

  async getUserByEmail(data: GetUserByEmail): Promise<User | null> {
    const response = await this.storageConfig.clickHouse.query({
      query: 'SELECT * FROM users WHERE email={email: VARCHAR}',
      query_params: {
        email: data.email,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as User[];
    if (parsedResponse.length === 0) {
      return Promise.resolve(null);
    }

    return Promise.resolve(parsedResponse[0]);
  }

  async batchInsert(): Promise<void> {
    if (this.storageConfig.clickHouse !== undefined && this.localEvents.length > 0) {
      this.storageConfig.clickHouse.insert({
        table: 'events',
        values: [...this.localEvents],
        format: 'JSONEachRow',
      }).then(() => {
        console.info('Batch insert of', this.localEvents.length, 'events finished');
        this.localEvents = [];
        return Promise.resolve();
      }).catch((err) => {
        console.error('Failed to insert', this.localEvents.length, 'events');
        if (this.storageConfig.discardEventsOnInsertError) {
          this.localEvents = [];
        }
        return Promise.reject(err);
      });
    }
  }

  async backup(): Promise<void> {
    const s3Destination = `${this.storageConfig.backupToS3.s3Url}/${dayjs().format('YYYY_MM_DD_HH_mm_ss')}`;
    const backupStartTime = performance.now();
    console.info('Backup to S3 started', s3Destination);
    this.storageConfig.clickHouse.exec({
      query: `BACKUP DATABASE ${this.storageConfig.backupToS3.databaseToBackup} TO S3('${s3Destination}', '${this.storageConfig.backupToS3.accessKey}', '${this.storageConfig.backupToS3.secretKey}');`,
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
      await this.storageConfig.clickHouse.exec({
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

    const response = await this.storageConfig.clickHouse.query({
      query,
      query_params: {
        dateFrom: dayjs(dateFrom).utc().format('YYYY-MM-DD HH:mm:ss'),
        dateTo: dateTo ? dayjs(dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        type: type || '',
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
    let query = 'SELECT COUNT(DISTINCT fingerprint) as online FROM events WHERE (created_at > now() - INTERVAL {onlineTimespan: INT} MINUTE)';

    if (pathname) {
      query += ' AND pathname={pathname: VARCHAR}';
    }

    const response = await this.storageConfig.clickHouse.query({
      query,
      query_params: {
        onlineTimespan: this.storageConfig.onlineTimespan,
        pathname: pathname || '',
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as {online: number}[];
    const online = Number(parsedResponse[0].online);
    console.info('Found', online, 'online users in the last', this.storageConfig.onlineTimespan, 'minutes');
    return Promise.resolve(online);
  }

  async getPathnames({ dateTo, dateFrom }: Timespan): Promise<Pathname[]> {
    let query = 'SELECT pathname, COUNT(*) count FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    query += ' GROUP BY pathname ORDER BY count DESC';

    const response = await this.storageConfig.clickHouse.query({
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

    const response = await this.storageConfig.clickHouse.query({
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

  async addUser(user: AddUser): Promise<void> {
    try {
      await this.storageConfig.clickHouse.insert({
        table: 'users',
        values: [{
          email: user.email,
          password: user.passwordHash,
          salt: user.salt,
        }],
        format: 'JSONEachRow',
      });

      return Promise.resolve();
    } catch (err) {
      console.error('Inserting user failed', err);
      return Promise.reject(err);
    }
  }
}
