import dayjs from 'dayjs';
import { CronJob } from 'cron';
import { ClickHouseClient } from '@clickhouse/client';
import {
  AddEvent,
  CountOnline,
  EventsExplorer,
  EventsRepositoryData,
  PathnamesPopularity,
  SearchForEvents,
  Timespan,
} from '../eventsRepositoryData';
import { Event } from '../../domain/event';
import { Pathname } from '../../domain/pathname';
import { EventType } from '../../domain/eventType';
import { EventsExplorerData } from '../../domain/eventsExplorerData';
import { Summary } from '../../domain/summary';

export type EventEntity = {
  pathname: string,
  fingerprint: string,
  type: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
  created_at?: string,
}

export type EventsRepositoryConfig = {
  clickHouse: ClickHouseClient;
  cronInsertPattern: string;
  discardEventsOnInsertError: boolean;
  onlineTimespan: number;
}

function appendInterval(interval: string): string {
  if (interval === 'month') {
    return 'toStartOfMonth(created_at) as date';
  }

  if (interval === 'week') {
    return 'toStartOfWeek(created_at) as date';
  }

  if (interval === 'day') {
    return 'toStartOfDay(created_at) as date';
  }

  if (interval === 'hour') {
    return 'toStartOfHour(created_at) as date';
  }

  if (interval === 'minute') {
    return 'toStartOfMinute(created_at) as date';
  }

  return 'toStartOfYear(created_at) as date';
}

export default class EventsRepository implements EventsRepositoryData {
  private config: EventsRepositoryConfig;

  private localEvents: EventEntity[];

  constructor(config: EventsRepositoryConfig) {
    this.config = config;

    this.localEvents = [];

    const insertCron = new CronJob(
      this.config.cronInsertPattern,
      () => this.batchInsert(),
      null,
    );
    insertCron.start();
  }

  async batchInsert(): Promise<void> {
    if (!this.config.clickHouse) return;

    if (this.localEvents.length > 0) {
      try {
        await this.config.clickHouse.insert({
          table: 'events',
          values: [...this.localEvents],
          format: 'JSONEachRow',
        });
        console.info('Batch insert of', this.localEvents.length, 'events finished');
        this.localEvents = [];
      } catch (err) {
        console.error('Failed to insert', this.localEvents.length, 'events');
        if (this.config.discardEventsOnInsertError) {
          this.localEvents = [];
        }
      }
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

    const response = await this.config.clickHouse.query({
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

    const response = await this.config.clickHouse.query({
      query,
      query_params: {
        onlineTimespan: this.config.onlineTimespan,
        pathname: pathname || '',
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as {online: string}[];
    const online = Number(parsedResponse[0].online);
    console.info('Found', online, 'online users in the last', this.config.onlineTimespan, 'minutes');
    return Promise.resolve(online);
  }

  async getPathnames({ dateTo, dateFrom }: Timespan): Promise<Pathname[]> {
    let query = 'SELECT pathname, COUNT(*) count FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    query += ' GROUP BY pathname ORDER BY count DESC';

    const response = await this.config.clickHouse.query({
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

  async getTypes({ dateTo, dateFrom }: Timespan): Promise<EventType[]> {
    let query = 'SELECT type, COUNT(*) count FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    query += ' GROUP BY type ORDER BY count DESC';

    const response = await this.config.clickHouse.query({
      query,
      query_params: {
        dateTo: dateTo ? dayjs(dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as EventType[];
    return Promise.resolve(parsedResponse);
  }

  async getEventsCount(data: EventsExplorer): Promise<EventsExplorerData> {
    let query = 'SELECT COUNT(id) as count, ';

    query += appendInterval(data.interval);

    query += ' FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (data.dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    if (data.os !== 'all') {
      query += " AND meta['os'] = {os: VARCHAR}";
    }

    query += ' GROUP BY date ORDER BY date ASC';
    const response = await this.config.clickHouse.query({
      query,
      query_params: {
        dateTo: data.dateTo ? dayjs(data.dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom: data.dateFrom,
        os: data.os,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as {count: string, date: string}[];
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
    const response = await this.config.clickHouse.query({
      query,
      query_params: {
        dateTo: data.dateTo ? dayjs(data.dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom: data.dateFrom,
        os: data.os,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as {count: string, pathname: string}[];
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

    query += appendInterval(data.interval);

    query += ' FROM events WHERE created_at >= {dateFrom: DATETIME}';

    if (data.dateTo) {
      query += ' AND created_at <= {dateTo: DATETIME}';
    }

    if (data.os !== 'all') {
      query += " AND meta['os'] = {os: VARCHAR}";
    }

    query += ' GROUP BY date ORDER BY date ASC';
    const response = await this.config.clickHouse.query({
      query,
      query_params: {
        dateTo: data.dateTo ? dayjs(data.dateTo).utc().format('YYYY-MM-DD HH:mm:ss') : '',
        dateFrom: data.dateFrom,
        os: data.os,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as {count: string, date: string}[];
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

  async getSummary(): Promise<Summary> {
    const responseOnline = await this.config.clickHouse.query({
      query: 'SELECT COUNT(DISTINCT fingerprint) as online FROM events WHERE (created_at > now() - INTERVAL {onlineTimespan: INT} MINUTE)',
      query_params: {
        onlineTimespan: this.config.onlineTimespan,
      },
      format: 'JSONEachRow',
    });

    const parsedResponseOnline = await responseOnline.json() as {online: string}[];
    const online = Number(parsedResponseOnline[0].online);

    const response24h = await this.config.clickHouse.query({
      query: 'SELECT COUNT(DISTINCT(fingerprint)) as unique, COUNT(id) as events FROM events WHERE created_at > NOW() - INTERVAL 1 DAY;',
      query_params: {
        onlineTimespan: this.config.onlineTimespan,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse24h = await response24h.json() as {unique: string, events: string}[];
    const unique = Number(parsedResponse24h[0].unique);
    const events = Number(parsedResponse24h[0].events);

    return Promise.resolve({ online, events, unique });
  }
}
