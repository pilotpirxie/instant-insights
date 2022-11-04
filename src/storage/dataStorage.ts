import { Event } from "../domain/event";

export type AddEventType = Omit<Event, "id" | "createdAt">;
export type SearchForEventsType = {
  type?: string,
  limit: number,
  dateFrom: Date,
  dateTo?: Date
}

export interface DataStorage {
  addEvent(event: AddEventType): Promise<void>;
  getEvents(search: SearchForEventsType): Promise<Event[]>;
}
