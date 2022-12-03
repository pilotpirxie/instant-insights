import express, { Express } from 'express';
import dotenv from 'dotenv';
import bodyParser from 'body-parser';
import { createClient } from '@clickhouse/client';
import path from 'path';
import crypto from 'crypto';
import { errorHandler } from './middlewares/errors';
import { ClickHouseStorage } from './storage/clickHouse/clickHouseStorage';
import { initializeEventsController } from './controllers/events';
import { initializeOnlineController } from './controllers/online';
import { initializeUsersController } from './controllers/users';

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

const clickHouseStorage = new ClickHouseStorage({
  clickHouse: clickHouseClient,
  onlineTimespan: Number(process.env.ONLINE_TIMESPAN || 5),
  discardEventsOnInsertError: process.env.DISCARD_EVENTS_ON_INSERT_ERROR === 'true',
  cronInsertPattern: process.env.CRON_INSERT_PATTERN || '* * * * * *',
  backupToS3: {
    secretKey: process.env.BACKUP_TO_S3_SECRET_KEY || '',
    accessKey: process.env.BACKUP_TO_S3_ACCESS_KEY || '',
    s3Url: process.env.BACKUP_TO_S3_URL || '',
    cronPattern: process.env.BACKUP_TO_S3_CRON_PATTERN || '0 0 */1 * *',
    enable: process.env.BACKUP_TO_S3_ENABLE === 'true',
    databaseToBackup: process.env.CLICKHOUSE_NAME || '',
  },
});

clickHouseStorage.migrate(path.join(__dirname, 'migrations')).then(() => {
  console.info('Migration has been completed');
}).catch((err) => {
  console.error('Migration failed', err);
  process.exit(1);
});

if (process.env.USER_EMAIL && process.env.USER_PASSWORD) {
  console.info('Creating user', process.env.USER_EMAIL);

  const salt = crypto.randomBytes(16).toString('hex');
  const passwordHash = crypto.pbkdf2Sync(process.env.USER_PASSWORD, salt, 1000, 64, 'sha512').toString('hex');

  clickHouseStorage.getUserByEmail({ email: process.env.USER_EMAIL })
    .then((user) => {
      if (!user) {
        return clickHouseStorage.addUser({
          email: process.env.USER_EMAIL || '',
          passwordHash,
          salt,
        });
      }
      console.info('User already exists, skipping');
    }).then(() => {
      console.info('User has been created');
    }).catch((err) => {
      console.error('Failed to get user', err);
      process.exit(1);
    });
} else {
  console.info('No user credentials provided. Skipping default user creation');
}

app.use(bodyParser.json({ limit: process.env.MAX_EVENT_SIZE || '1KB' }));

app.use('/api/events', initializeEventsController({ dataStorage: clickHouseStorage }));
app.use('/api/online', initializeOnlineController({ dataStorage: clickHouseStorage }));
app.use('/api/users', initializeUsersController({
  dataStorage: clickHouseStorage,
  jwtSecret: process.env.JWT_SECRET || '',
  tokenExpiresIn: Number(process.env.TOKEN_EXPIRE_IN || 86400),
  refreshTokenExpiresIn: Number(process.env.REFRESH_TOKEN_EXPIRE_IN || 604800),
}));

app.use(errorHandler);

app.listen(port, () => {
  // eslint-disable-next-line no-console
  console.info(`Server is running on port ${port}`);
});
