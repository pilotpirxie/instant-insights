import 'dotenv';
import { ClickHouseClient } from '@clickhouse/client';
import path from 'path';
import fs from 'fs';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import {
  AddEventType, CountOnlineType, DataStorage, SearchForEventsType,
} from '../dataStorage';
import { EventEntity } from './entities';
import { Event } from '../../domain/event';

dayjs.extend(utc);

export class ClickHouseStorage implements DataStorage {
  private clickHouse: ClickHouseClient;

  private localEvents: EventEntity[];

  constructor(clickHouse: ClickHouseClient) {
    this.clickHouse = clickHouse;
    this.localEvents = [];
  }

  async migrate(dir: string): Promise<void> {
    const files = fs.readdirSync(dir);
    files.sort((a, b) => Number(a.split('_')[0]) - Number(b.split('_')[0]));
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const migrationFile = path.join(dir, file);

      console.info(`Migration ${migrationFile}`);
      const migrationFileContent = fs.readFileSync(migrationFile, { encoding: 'utf8' });
      await this.clickHouse.exec({
        query: migrationFileContent,
      });
    }
  }

  async addEvent(event: AddEventType): Promise<void> {
    this.localEvents.push({
      pathname: event.pathname,
      fingerprint: event.fingerprint,
      type: event.type,
      meta: event.meta,
      params: event.params,
      created_at: dayjs().utc().format('YYYY-MM-DD HH:mm:ss'),
    });

    const batchInsertMax = Number(process.env.BATCH_INSERT_EVERY || 100);

    if (this.localEvents.length >= batchInsertMax) {
      this.clickHouse.insert({
        table: 'events',
        values: this.localEvents,
        format: 'JSONEachRow',
      }).then(() => {
        console.info('Inserted batch of', batchInsertMax, 'events');
        this.localEvents = [];
        return Promise.resolve();
      }).catch((err) => {
        console.error('Failed to insert', batchInsertMax, 'events');
        if (process.env.DISCARD_EVENTS_ON_INSERT_ERROR === '1') {
          this.localEvents = [];
        }
        return Promise.reject(err);
      });
    }
    return Promise.resolve();
  }

  async getEvents({
    pathname, type, fingerprint, dateFrom, dateTo, limit,
  }: SearchForEventsType): Promise<Event[]> {
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

    query += ' LIMIT {limit: INT}';

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

  async countOnline({ pathname }: CountOnlineType): Promise<number> {
    const onlineTimespan = Number(process.env.ONLINE_TIMESPAN || 5);

    let subquery = 'SELECT DISTINCT(fingerprint) fingerprint FROM insights.events WHERE (created_at > now() - INTERVAL {onlineTimespan: INT} MINUTE)';

    if (pathname) {
      subquery += ' AND pathname={pathname: VARCHAR}';
    }

    const query = `SELECT COUNT(fingerprint) as online FROM (${subquery})`;

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
}
