import express, { Express } from "express";
import dotenv from "dotenv";
import bodyParser from "body-parser";
import { createClient } from "@clickhouse/client";
import path from "path";
import dayjs from "dayjs";
import { errorHandler } from "./middlewares/errors";
import { ClickHouseStorage } from "./storage/clickHouse/clickHouseStorage";

dotenv.config();
const port = process.env.PORT || 3000;
const app: Express = express();

const clickHouseClient = createClient({
  host: process.env.CLICKHOUSE_URL || "http://localhost:8123",
  username: process.env.CLICKHOUSE_USER || "default",
  password: process.env.CLICKHOUSE_PASS || "",
  database: process.env.CLICKHOUSE_NAME || "default",
  connect_timeout: Number(process.env.CLICKHOUSE_CONNECT_TIMEOUT || 10000),
  request_timeout: Number(process.env.CLICKHOUSE_REQUEST_TIMEOUT || 30000),
  max_open_connections: Number(process.env.CLICKHOUSE_MAX_OPEN_CONNECTIONS || Infinity),
  log: {
    enable: true,
  },
});

const clickHouseStorage = new ClickHouseStorage(clickHouseClient);

clickHouseStorage.migrate(path.join(__dirname, "migrations")).then(() => {
  console.info("Migration has been completed");
}).catch((err) => {
  console.error("Migration failed", err);
  process.exit(1);
});

clickHouseStorage.addEvent({
  appId: "2009c796-46be-423f-a590-8bb8078d0dcd",
  type: "click2",
  params: {
    a: "1",
    b: "2",
    c: "3",
  },
  user: {
    x: "1",
    d: "2",
    c: "3",
  },
}).then(async () => {
  const events = await clickHouseStorage.getEvents({
    appId: "2009c796-46be-423f-a590-8bb8078d0dcd",
    dateFrom: dayjs().subtract(10, "minute").toDate(),
    limit: 2,
    type: "click2",
  });

  // eslint-disable-next-line no-console
  console.table(events);
});

app.use(bodyParser.json());

app.use(errorHandler);

app.listen(port, () => {
  // eslint-disable-next-line no-console
  console.info(`Server is running on port ${port}`);
});
