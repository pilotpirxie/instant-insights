import express, { Express } from 'express';
import dotenv from 'dotenv';
import bodyParser from 'body-parser';
import { createClient } from '@clickhouse/client';
import path from 'path';
import { errorHandler } from './middlewares/errors';
import { migrate } from './utils/migrationHelper';

dotenv.config();
const port = process.env.PORT || 3000;
const app: Express = express();

const clickHouseClient = createClient({
  host: process.env.CLICKHOUSE_URL || 'http://localhost:8123',
  username: process.env.CLICKHOUSE_USER || 'default',
  password: process.env.CLICKHOUSE_PASS || '',
  database: process.env.CLICKHOUSE_NAME || 'default',
  connect_timeout: Number(process.env.CLICKHOUSE_CONNECT_TIMEOUT || 10000),
  request_timeout: Number(process.env.CLICKHOUSE_REQUEST_TIMEOUT || 30000),
  max_open_connections: Number(process.env.CLICKHOUSE_MAX_OPEN_CONNECTIONS || Infinity),
});

migrate(clickHouseClient, path.join(__dirname, 'migrations')).then(() => {
  console.info('Migration complete');
}).catch((err) => {
  console.error('Migration failed', err);
  process.exit(1);
});

app.use(bodyParser.json());

app.use(errorHandler);

app.listen(port, () => {
  // eslint-disable-next-line no-console
  console.info(`Server is running on port ${port}`);
});
