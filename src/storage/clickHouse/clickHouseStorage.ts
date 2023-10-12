import 'dotenv';
import {ClickHouseClient} from '@clickhouse/client';
import path from 'path';
import fs from 'fs';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import {CronJob} from 'cron';
import {
  AddEvent,
  AddLinkHit,
  AddSession,
  AddUser,
  CountOnline,
  DataStorage,
  EventsExplorer,
  GetSessionByRefreshToken,
  GetUserByEmail,
  PathnamesPopularity,
  SearchForEvents,
  Timespan,
  UpdateSession,
} from '../dataStorage';
import {EventEntity, LinkHitEntity} from './entities';
import {Event} from '../../domain/event';
import {Pathname} from '../../domain/pathname';
import {Type} from '../../domain/type';
import {User} from '../../domain/user';
import {Session} from '../../domain/session';
import {Summary} from '../../domain/summary';
import {EventsExplorerData} from '../../domain/eventsExplorerData';
import Links from '../../domain/links';
import RedirectLink from '../../domain/redirectLink';

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

  private localLinkHits: LinkHitEntity[];

  constructor(storageConfig: ClickHouseStorageConfig) {
    this.storageConfig = storageConfig;

    this.localEvents = [];
    this.localLinkHits = [];

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

    const deleteOldEventsCron = new CronJob(
      '0 0 * * *',
      () => this.deleteEventsOlderThan(dayjs().utc().subtract(3, 'month').toDate()),
      null,
    );
    deleteOldEventsCron.start();
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
    if (!this.storageConfig.clickHouse) return;

    if (this.localEvents.length > 0) {
      try {
        await this.storageConfig.clickHouse.insert({
          table: 'events',
          values: [...this.localEvents],
          format: 'JSONEachRow',
        });
        console.info('Batch insert of', this.localEvents.length, 'events finished');
        this.localEvents = [];
      } catch (err) {
        console.error('Failed to insert', this.localEvents.length, 'events');
        if (this.storageConfig.discardEventsOnInsertError) {
          this.localEvents = [];
        }
      }
    }

    if (this.localLinkHits.length > 0) {
      try {
        await this.storageConfig.clickHouse.insert({
          table: 'link_hits',
          values: [...this.localLinkHits],
          format: 'JSONEachRow',
        });
        console.info('Batch insert of', this.localLinkHits.length, 'link hits finished');
        this.localLinkHits = [];

        return Promise.resolve();
      } catch (err) {
        console.error('Failed to insert', this.localLinkHits.length, 'link hits');
        if (this.storageConfig.discardEventsOnInsertError) {
          this.localLinkHits = [];
        }
      }
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
      const migrationFileContent = fs.readFileSync(migrationFile, {encoding: 'utf8'});
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
    const response = this.storageConfig.clickHouse.query({
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

  async countOnline({pathname}: CountOnline): Promise<number> {
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

    const parsedResponse = await response.json() as { online: string }[];
    const online = Number(parsedResponse[0].online);
    console.info('Found', online, 'online users in the last', this.storageConfig.onlineTimespan, 'minutes');
    return Promise.resolve(online);
  }

  async getPathnames({dateTo, dateFrom}: Timespan): Promise<Pathname[]> {
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

  async getTypes({dateTo, dateFrom}: Timespan): Promise<Type[]> {
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
  }

  async getSessionByRefreshToken(data: GetSessionByRefreshToken): Promise<Session | null> {
    const response = await this.storageConfig.clickHouse.query({
      query: 'SELECT * FROM sessions WHERE refresh_token={refreshToken: VARCHAR}',
      query_params: {
        refreshToken: data.refreshToken,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as Session[];
    if (parsedResponse.length === 0) {
      return Promise.resolve(null);
    }

    return Promise.resolve(parsedResponse[0]);
  }

  async updateSession(data: UpdateSession): Promise<void> {
    await this.storageConfig.clickHouse.query({
      query: 'ALTER TABLE sessions UPDATE refresh_token={refreshToken: VARCHAR}, expires_at={expiresAt: VARCHAR} WHERE id={id: VARCHAR}',
      query_params: {
        refreshToken: data.newRefreshToken,
        expiresAt: dayjs(data.expiresAt).utc().format('YYYY-MM-DD HH:mm:ss'),
        id: data.id,
      },
    });

    return Promise.resolve();
  }

  async getSummary(): Promise<Summary> {
    const responseOnline = await this.storageConfig.clickHouse.query({
      query: 'SELECT COUNT(DISTINCT fingerprint) as online FROM events WHERE (created_at > now() - INTERVAL {onlineTimespan: INT} MINUTE)',
      query_params: {
        onlineTimespan: this.storageConfig.onlineTimespan,
      },
      format: 'JSONEachRow',
    });

    const parsedResponseOnline = await responseOnline.json() as { online: string }[];
    const online = Number(parsedResponseOnline[0].online);

    const response24h = await this.storageConfig.clickHouse.query({
      query: 'SELECT COUNT(DISTINCT(fingerprint)) as unique, COUNT(id) as events FROM events WHERE created_at > NOW() - INTERVAL 1 DAY;',
      query_params: {
        onlineTimespan: this.storageConfig.onlineTimespan,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse24h = await response24h.json() as { unique: string, events: string }[];
    const unique = Number(parsedResponse24h[0].unique);
    const events = Number(parsedResponse24h[0].events);

    return Promise.resolve({online, events, unique});
  }

  async getEventsCount(data: EventsExplorer): Promise<EventsExplorerData> {
    let query = 'SELECT COUNT(id) as count, ';

    if (data.interval === 'year') {
      query += 'toStartOfYear(created_at) as date';
    }

    if (data.interval === 'month') {
      query += 'toStartOfMonth(created_at) as date';
    }

    if (data.interval === 'week') {
      query += 'toStartOfWeek(created_at) as date';
    }

    if (data.interval === 'day') {
      query += 'toStartOfDay(created_at) as date';
    }

    if (data.interval === 'hour') {
      query += 'toStartOfHour(created_at) as date';
    }

    if (data.interval === 'minute') {
      query += 'toStartOfMinute(created_at) as date';
    }

    query += ' FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (data.dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    if (data.os !== 'all') {
      query += " AND meta['os'] = {os: VARCHAR}";
    }

    query += ' GROUP BY date ORDER BY date ASC';
    const response = await this.storageConfig.clickHouse.query({
      query,
      query_params: {
        dateTo: data.dateTo ? dayjs(data.dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom: data.dateFrom,
        os: data.os,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as { count: string, date: string }[];
    const result: EventsExplorerData = {
      data: [],
      labels: [],
    };

    parsedResponse.forEach((item) => {
      result.labels.push(item.date);
      result.data.push(Number(item.count));
    });
    return Promise.resolve(result);
  }

  async getPathnamesPopularity(data: PathnamesPopularity): Promise<EventsExplorerData> {
    let query = 'SELECT COUNT(id) as count, pathname FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (data.dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    if (data.os !== 'all') {
      query += " AND meta['os'] = {os: VARCHAR}";
    }

    query += ' GROUP BY pathname ORDER BY count DESC';

    const response = await this.storageConfig.clickHouse.query({
      query,
      query_params: {
        dateTo: data.dateTo ? dayjs(data.dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom: data.dateFrom,
        os: data.os,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as { count: string, pathname: string }[];
    const result: EventsExplorerData = {
      data: [],
      labels: [],
    };

    parsedResponse.forEach((item) => {
      result.labels.push(item.pathname);
      result.data.push(Number(item.count));
    });
    return Promise.resolve(result);
  }

  async getUsersActivity(data: EventsExplorer): Promise<EventsExplorerData> {
    let query = 'SELECT COUNT(DISTINCT fingerprint) as count, ';

    if (data.interval === 'year') {
      query += 'toStartOfYear(created_at) as date';
    }

    if (data.interval === 'month') {
      query += 'toStartOfMonth(created_at) as date';
    }

    if (data.interval === 'week') {
      query += 'toStartOfWeek(created_at) as date';
    }

    if (data.interval === 'day') {
      query += 'toStartOfDay(created_at) as date';
    }

    if (data.interval === 'hour') {
      query += 'toStartOfHour(created_at) as date';
    }

    if (data.interval === 'minute') {
      query += 'toStartOfMinute(created_at) as date';
    }

    query += ' FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (data.dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    if (data.os !== 'all') {
      query += " AND meta['os'] = {os: VARCHAR}";
    }

    query += ' GROUP BY date ORDER BY date ASC';
    const response = await this.storageConfig.clickHouse.query({
      query,
      query_params: {
        dateTo: data.dateTo ? dayjs(data.dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom: data.dateFrom,
        os: data.os,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as { count: string, date: string }[];
    const result: EventsExplorerData = {
      data: [],
      labels: [],
    };

    parsedResponse.forEach((item) => {
      result.labels.push(item.date);
      result.data.push(Number(item.count));
    });
    return Promise.resolve(result);
  }

  async deleteEventsOlderThan(date: Date): Promise<void> {
    await this.storageConfig.clickHouse.query({
      query: 'ALTER TABLE events DELETE WHERE created_at < {date: DATETIME}',
      query_params: {
        date: dayjs(date).utc().format('YYYY-MM-DD HH:mm:ss'),
      },
    });

    return Promise.resolve();
  }
}
