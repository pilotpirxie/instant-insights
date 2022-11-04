import { Event } from "../domain/event";

export type AddEventType = Omit<Event, "id" | "createdAt">;
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
