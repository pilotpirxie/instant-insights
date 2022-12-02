import { Router } from 'express';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import Joi from 'joi';
import crypto from 'crypto';
import jwt from 'jsonwebtoken';
import { DataStorage } from '../storage/dataStorage';
import { TypedRequest } from '../types/express';

dayjs.extend(utc);

type OnlineControllerParams = {
  jwtSecret: string;
  tokenExpiresIn: string;
  refreshTokenExpiresIn: string;
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

  router.post('/', async (req: TypedRequest<typeof loginSchema>, res, next) => {
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

      const token = jwt.sign({ sub: user.id }, jwtSecret, {
        algorithm: 'HS256',
        expiresIn: tokenExpiresIn,
      });

      const refreshToken = jwt.sign({ sub: user.id }, jwtSecret, {
        algorithm: 'HS256',
        expiresIn: refreshTokenExpiresIn,
      });

      return res.json({
        token,
        refreshToken,
        tokenExpiresIn,
        refreshTokenExpiresIn,
      });
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
