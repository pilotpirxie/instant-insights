import path from 'path';
import fs from 'fs';
import { ClickHouseClient } from '@clickhouse/client';

export async function migrate(clickHouseClient: ClickHouseClient, dir: string) {
  const files = fs.readdirSync(dir);
  files.sort((a, b) => Number(a.split('_')[0]) - Number(b.split('_')[0]));
  for (const file of files) {
    const migrationFile = path.join(dir, file);
    console.info(`Migration ${migrationFile}`);
    const migrationFileContent = fs.readFileSync(migrationFile, { encoding: 'utf8' });
    await clickHouseClient.exec({
      query: migrationFileContent,
    });
  }
}
