import "dotenv";
import { ClickHouseClient } from "@clickhouse/client";
import path from "path";
import fs from "fs";
import dayjs from "dayjs";
import { AddEventType, DataStorage, SearchForEventsType } from "../dataStorage";
import { EventEntity } from "./dataTypes";
import { Event } from "../../domain/event";

export class ClickHouseStorage implements DataStorage {
  private clickHouse: ClickHouseClient;

  private localEvents: EventEntity[];

  constructor(clickHouse: ClickHouseClient) {
    this.clickHouse = clickHouse;
    this.localEvents = [];
  }

  async migrate(dir: string): Promise<void> {
    const files = fs.readdirSync(dir);
    files.sort((a, b) => Number(a.split("_")[0]) - Number(b.split("_")[0]));
    for (const file of files) {
      const migrationFile = path.join(dir, file);
      console.info(`Migration ${migrationFile}`);
      const migrationFileContent = fs.readFileSync(migrationFile, { encoding: "utf8" });
      await this.clickHouse.exec({
        query: migrationFileContent,
      });
    }
  }

  async addEvent(event: AddEventType): Promise<void> {
    this.localEvents.push({
      id: undefined,
      app_id: event.appId,
      type: event.type,
      user: event.user,
      params: event.params,
      created_at: dayjs().format("YYYY-MM-DD HH:mm:ss"),
    });

    const batchInsertMax = Number(process.env.BATCH_INSERT_MAX || 100);

    if (this.localEvents.length >= batchInsertMax) {
      this.clickHouse.insert({
        table: "events",
        values: this.localEvents,
        format: "JSONEachRow",
      }).then(() => {
        console.info("Inserted batch of", batchInsertMax, "events");
        this.localEvents = [];
        return Promise.resolve();
      }).catch((err) => {
        console.error("Failed to insert", batchInsertMax, "events");
        if (process.env.DROP_EVENTS_ON_INSERT_ERROR === "1") {
          this.localEvents = [];
        }
        return Promise.reject(err);
      });
    }
    return Promise.resolve();
  }

  async getEvents(search: SearchForEventsType): Promise<Event[]> {
    let query = "SELECT * FROM events WHERE app_id={appIdParam: UUID} AND created_at >= {dateFromParam: DATETIME}";

    if (search.dateTo) {
      query += " AND created_at <= {dateToParam: DATETIME}";
    }

    if (search.type) {
      query += " AND type = {typeParam: VARCHAR}";
    }

    query += " LIMIT {limitParam: INT}";

    const events = await this.clickHouse.query({
      query,
      query_params: {
        appIdParam: search.appId,
        dateFromParam: dayjs(search.dateFrom).format("YYYY-MM-DD HH:mm:ss"),
        dateToParam: search.dateTo ? dayjs(search.dateTo).format("YYYY-MM-DD HH:mm:ss") : "",
        typeParam: search.type || "",
        limitParam: search.limit || 100,
      },
      format: "JSONEachRow",
    });
    const eventsData = await events.json() as Event[];
    console.info("Found", eventsData.length, "events");
    return Promise.resolve(eventsData);
  }
}
