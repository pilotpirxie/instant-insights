import dayjs from 'dayjs';
import { ClickHouseClient } from '@clickhouse/client';
import {
  AddSession,
  AddUser,
  GetSessionByRefreshToken,
  GetUserByEmail,
  IUsersRepository,
  UpdateSession,
} from '../IUsersRepository';
import { User } from '../../domain/user';
import { Session } from '../../domain/session';

export type UsersRepositoryConfig = {
  clickHouse: ClickHouseClient;
}

export class UsersRepository implements IUsersRepository {
  private config: UsersRepositoryConfig;

  constructor(config: UsersRepositoryConfig) {
    this.config = config;
  }

  async addSession(data: AddSession): Promise<void> {
    try {
      await this.config.clickHouse.insert({
        table: 'sessions',
        values: [{
          user_id: data.userId,
          expires_at: dayjs(data.expiresAt).utc().format('YYYY-MM-DD HH:mm:ss'),
          refresh_token: data.refreshToken,
          ip: data.ip,
        }],
        format: 'JSONEachRow',
      });

      return Promise.resolve();
    } catch (err) {
      console.error('Inserting session failed', err);
      return Promise.reject(err);
    }
  }

  async getUserByEmail(data: GetUserByEmail): Promise<User | null> {
    const response = await this.config.clickHouse.query({
      query: 'SELECT * FROM users WHERE email={email: VARCHAR}',
      query_params: {
        email: data.email,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as User[];
    if (parsedResponse.length === 0) {
      return Promise.resolve(null);
    }

    return Promise.resolve(parsedResponse[0]);
  }

  async addUser(user: AddUser): Promise<void> {
    await this.config.clickHouse.insert({
      table: 'users',
      values: [{
        email: user.email,
        password: user.passwordHash,
        salt: user.salt,
      }],
      format: 'JSONEachRow',
    });

    return Promise.resolve();
  }

  async getSessionByRefreshToken(data: GetSessionByRefreshToken): Promise<Session | null> {
    const response = await this.config.clickHouse.query({
      query: 'SELECT * FROM sessions WHERE refresh_token={refreshToken: VARCHAR}',
      query_params: {
        refreshToken: data.refreshToken,
      },
      format: 'JSONEachRow',
    });

    const parsedResponse = await response.json() as Session[];
    if (parsedResponse.length === 0) {
      return Promise.resolve(null);
    }

    return Promise.resolve(parsedResponse[0]);
  }

  async updateSession(data: UpdateSession): Promise<void> {
    await this.config.clickHouse.query({
      query: 'ALTER TABLE sessions UPDATE refresh_token={refreshToken: VARCHAR}, expires_at={expiresAt: VARCHAR} WHERE id={id: VARCHAR}',
      query_params: {
        refreshToken: data.newRefreshToken,
        expiresAt: dayjs(data.expiresAt).utc().format('YYYY-MM-DD HH:mm:ss'),
        id: data.id,
      },
    });

    return Promise.resolve();
  }
}
