import { Event } from '../domain/event';

export type AddEventType = {
  appId: number,
  pathname: string,
  fingerprint: string,
  type: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
};

export type SearchForEventsType = {
  appId: number,
  type?: string,
  pathname?: string,
  limit: number,
  dateFrom: Date,
  dateTo?: Date
  fingerprint?: string
}

export type CountOnlineType = {
  appId: number,
  pathname?: string,
}

export interface DataStorage {
  addEvent(event: AddEventType): Promise<void>;
  getEvents(search: SearchForEventsType): Promise<Event[]>;
  countOnline(online: CountOnlineType): Promise<number>;
}
