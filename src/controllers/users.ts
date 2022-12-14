import { Router } from 'express';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import Joi from 'joi';
import crypto from 'crypto';
import jwt from 'jsonwebtoken';
import { DataStorage } from '../storage/dataStorage';
import { TypedRequest } from '../types/express';
import validation from '../middlewares/validation';

dayjs.extend(utc);

type OnlineControllerParams = {
  jwtSecret: string;
  tokenExpiresIn: number;
  refreshTokenExpiresIn: number;
  dataStorage: DataStorage,
}

export function initializeUsersController({
  dataStorage, refreshTokenExpiresIn, tokenExpiresIn, jwtSecret,
}: OnlineControllerParams): Router {
  const router = Router();

  const loginSchema = {
    body: {
      email: Joi.string().required(),
      password: Joi.string().required(),
    },
  };

  router.post('/login', validation(loginSchema), async (req: TypedRequest<typeof loginSchema>, res, next) => {
    try {
      const { email, password } = req.body;
      const user = await dataStorage.getUserByEmail({ email });

      if (!user) {
        return res.sendStatus(401);
      }

      const passwordHash = crypto.pbkdf2Sync(password, user.salt, 1000, 64, 'sha512').toString('hex');
      if (user.password !== passwordHash) {
        return res.sendStatus(401);
      }

      const tokenExpiresAt = dayjs().add(tokenExpiresIn, 'seconds').unix() * 1000;
      const refreshTokenExpiresAt = dayjs().add(refreshTokenExpiresIn, 'seconds').unix() * 1000;

      const token = jwt.sign({ sub: user.id, aud: 'api' }, jwtSecret, {
        algorithm: 'HS256',
        expiresIn: tokenExpiresIn,
      });

      const refreshToken = jwt.sign({ sub: user.id, aud: 'refresh' }, jwtSecret, {
        algorithm: 'HS256',
        expiresIn: refreshTokenExpiresIn,
      });

      await dataStorage.addSession({
        refreshToken,
        userId: user.id,
        expiresAt: dayjs(refreshTokenExpiresAt).utc().toDate(),
        ip: req.headers['x-forwarded-for']?.toString() || req.socket.remoteAddress || '',
      });

      return res.json({
        token,
        refreshToken,
        tokenExpiresAt,
        refreshTokenExpiresAt,
      });
    } catch (e) {
      return next(e);
    }
  });

  const refreshTokenSchema = {
    body: {
      refreshToken: Joi.string().required(),
    },
  };

  router.post('/refresh-token', validation(refreshTokenSchema), async (req: TypedRequest<typeof refreshTokenSchema>, res, next) => {
    try {
      const { refreshToken } = req.body;
      const session = await dataStorage.getSessionByRefreshToken({ refreshToken });

      if (!session) {
        return res.sendStatus(401);
      }

      const tokenExpiresAt = dayjs().add(tokenExpiresIn, 'seconds').unix() * 1000;
      const refreshTokenExpiresAt = dayjs().add(refreshTokenExpiresIn, 'seconds').unix() * 1000;

      const token = jwt.sign({ sub: session.userId, aud: 'api' }, jwtSecret, {
        algorithm: 'HS256',
        expiresIn: tokenExpiresIn,
      });

      const newRefreshToken = jwt.sign({ sub: session.userId, aud: 'refresh' }, jwtSecret, {
        algorithm: 'HS256',
        expiresIn: refreshTokenExpiresIn,
      });

      await dataStorage.updateSession({
        id: session.id,
        newRefreshToken,
        expiresAt: dayjs(refreshTokenExpiresAt).utc().toDate(),
      });

      return res.json({
        token,
        refreshToken: newRefreshToken,
        tokenExpiresAt,
        refreshTokenExpiresAt,
      });
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
