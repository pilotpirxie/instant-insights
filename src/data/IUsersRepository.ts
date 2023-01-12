import { User } from '../domain/user';
import { Session } from '../domain/session';

export type GetUserByEmail = {
  email: string,
}

export type AddUser = {
  email: string,
  passwordHash: string,
  salt: string,
}

export type AddSession = {
  userId: string,
  expiresAt: Date,
  refreshToken: string,
  ip: string,
}

export type GetSessionByRefreshToken = {
  refreshToken: string,
}

export type UpdateSession = {
  id: string,
  expiresAt: Date,
  newRefreshToken: string,
}

export interface IUsersRepository {
  getUserByEmail(data: GetUserByEmail): Promise<User | null>;
  addSession(data: AddSession): Promise<void>;
  addUser(user: AddUser): Promise<void>;
  getSessionByRefreshToken(data: GetSessionByRefreshToken): Promise<Session | null>;
  updateSession(data: UpdateSession): Promise<void>;
}
