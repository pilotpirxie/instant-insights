import { Event } from '../domain/event';
import { Pathname } from '../domain/pathname';
import { EventType } from '../domain/eventType';
import { Summary } from '../domain/summary';
import { EventsExplorerData } from '../domain/eventsExplorerData';

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

export type EventsExplorer = {
  dateFrom: Date,
  dateTo?: Date,
  interval: string,
  os?: string,
}

export type PathnamesPopularity = {
  dateFrom: Date,
  dateTo?: Date,
  os?: string,
}

export interface EventsRepositoryData {
  addEvent(event: AddEvent): Promise<void>;
  getEvents(search: SearchForEvents): Promise<Event[]>;
  getPathnames(search: Timespan): Promise<Pathname[]>;
  getTypes(search: Timespan): Promise<EventType[]>;
  countOnline(online: CountOnline): Promise<number>;
  getSummary(): Promise<Summary>;
  getUsersActivity(data: EventsExplorer): Promise<EventsExplorerData>;
  getEventsCount(data: EventsExplorer): Promise<EventsExplorerData>;
  getPathnamesPopularity(data: PathnamesPopularity): Promise<EventsExplorerData>;
}
