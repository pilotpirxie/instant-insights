import 'dotenv';
import { ClickHouseClient } from '@clickhouse/client';
import path from 'path';
import fs from 'fs';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import { CronJob } from 'cron';
import { IMaintenance } from '../IMaintenance';

dayjs.extend(utc);

export type ClickHouseStorageConfig = {
  clickHouse: ClickHouseClient,
  backupToS3: {
    databaseToBackup: string,
    enable: boolean,
    cronPattern: string,
    s3Url: string,
    secretKey: string,
    accessKey: string,
  },
}

export class ClickHouseMaintenance implements IMaintenance {
  private storageConfig: ClickHouseStorageConfig;

  constructor(storageConfig: ClickHouseStorageConfig) {
    this.storageConfig = storageConfig;

    if (this.storageConfig.backupToS3.enable) {
      const backupCron = new CronJob(
        this.storageConfig.backupToS3.cronPattern,
        () => this.backup(),
        null,
      );
      backupCron.start();
      const nextBackupDates = backupCron.nextDates();
      console.info('Next backup run at', nextBackupDates.toFormat('yyyy-MM-dd HH:mm:ss'));
    }
  }

  async backup(): Promise<void> {
    const s3Destination = `${this.storageConfig.backupToS3.s3Url}/${dayjs().format('YYYY_MM_DD_HH_mm_ss')}`;
    const backupStartTime = performance.now();
    console.info('Backup to S3 started', s3Destination);
    this.storageConfig.clickHouse.exec({
      query: `BACKUP DATABASE ${this.storageConfig.backupToS3.databaseToBackup} TO S3('${s3Destination}', '${this.storageConfig.backupToS3.accessKey}', '${this.storageConfig.backupToS3.secretKey}');`,
    }).then(() => {
      const backupFinishTime = performance.now();
      console.info('Backup to S3 finished in', backupFinishTime - backupStartTime, 'ms');
    }).catch((err) => {
      console.error('Backup to S3 failed', err);
    });
  }

  async migrate(dir: string): Promise<void> {
    const files = fs.readdirSync(dir);
    files.sort((a, b) => Number(a.split('_')[0]) - Number(b.split('_')[0]));
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const migrationFile = path.join(dir, file);

      console.info(`Migrating ${migrationFile}`);
      const migrationFileContent = fs.readFileSync(migrationFile, { encoding: 'utf8' });
      await this.storageConfig.clickHouse.exec({
        query: migrationFileContent,
      });
    }
  }
}
