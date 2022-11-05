import { Event } from '../domain/event';

export type AddEvent = {
  pathname: string,
  fingerprint: string,
  type: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
};

export type SearchForEvents = {
  type?: string,
  pathname?: string,
  limit: number,
  dateFrom: Date,
  dateTo?: Date
  fingerprint?: string
}

export type Timespan = {
  dateFrom: Date,
  dateTo?: Date
}

export type CountOnline = {
  pathname?: string,
}

export interface DataStorage {
  addEvent(event: AddEvent): Promise<void>;
  getEvents(search: SearchForEvents): Promise<Event[]>;
  getPathnames(search: Timespan): Promise<string[]>;
  getTypes(search: Timespan): Promise<string[]>;
  countOnline(online: CountOnline): Promise<number>;
}
