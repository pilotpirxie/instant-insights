import 'dotenv';
import { Router } from 'express';
import Joi from 'joi';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import validation from '../middlewares/validation';
import { TypedRequest } from '../types/express';
import { DataStorage } from '../storage/dataStorage';

dayjs.extend(utc);

export function initializeOnlineController(dataStorage: DataStorage): Router {
  const router = Router();

  const getOnlineSchema = {
    query: {
      appId: Joi.number().required(),
      pathname: Joi.string(),
    },
  };

  router.get('/', validation(getOnlineSchema), async (req: TypedRequest<typeof getOnlineSchema>, res, next) => {
    try {
      const {
        pathname, appId,
      } = req.query;

      const online = await dataStorage.countOnline({
        pathname,
        appId,
      });
      return res.json({ online });
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
