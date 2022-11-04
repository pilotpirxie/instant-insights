import { Event } from "../domain/event";

export type AddEventType = {
  appId: number,
  type: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
};

export type SearchForEventsType = {
  appId: string,
  dateFrom: Date,
  limit: number,
  type?: string,
  dateTo?: Date
}

export interface DataStorage {
  addEvent(event: AddEventType): Promise<void>;
  getEvents(search: SearchForEventsType): Promise<Event[]>;
}
