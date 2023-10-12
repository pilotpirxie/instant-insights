import { Event } from '../domain/event';
import { Type } from '../domain/type';
import { Pathname } from '../domain/pathname';
import { User } from '../domain/user';
import { Session } from '../domain/session';
import { Summary } from '../domain/summary';
import { EventsExplorerData } from '../domain/eventsExplorerData';
import Links from '../domain/links';

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

export type GetUserByEmail = {
  email: string,
}

export type AddUser = {
  email: string,
  passwordHash: string,
  salt: string,
}

export type AddSession = {
  userId: string,
  expiresAt: Date,
  refreshToken: string,
  ip: string,
}

export type GetSessionByRefreshToken = {
  refreshToken: string,
}

export type UpdateSession = {
  id: string,
  expiresAt: Date,
  newRefreshToken: string,
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

export type AddLinkHit = {
  name: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
};

export interface DataStorage {
  migrate(dir: string): Promise<void>;
  backup(): Promise<void>;
  addEvent(event: AddEvent): Promise<void>;
  getEvents(search: SearchForEvents): Promise<Event[]>;
  getPathnames(search: Timespan): Promise<Pathname[]>;
  getTypes(search: Timespan): Promise<Type[]>;
  countOnline(online: CountOnline): Promise<number>;
  countOnline(online: CountOnline): Promise<number>;
  getUserByEmail(data: GetUserByEmail): Promise<User | null>;
  addSession(data: AddSession): Promise<void>;
  addUser(user: AddUser): Promise<void>;
  getSessionByRefreshToken(data: GetSessionByRefreshToken): Promise<Session | null>;
  updateSession(data: UpdateSession): Promise<void>;
  getSummary(): Promise<Summary>;
  getUsersActivity(data: EventsExplorer): Promise<EventsExplorerData>;
  getEventsCount(data: EventsExplorer): Promise<EventsExplorerData>;
  getPathnamesPopularity(data: PathnamesPopularity): Promise<EventsExplorerData>;
  getLinks(name: string): Promise<Links | null>;
  addLinkHit(data: AddLinkHit): Promise<void>;
}
