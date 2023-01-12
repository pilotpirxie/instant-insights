import { Router } from 'express';
import Joi from 'joi';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import validation from '../middlewares/validation';
import { TypedRequest } from '../types/express';
import { jwtVerifyMiddleware } from '../middlewares/jwt';
import { IEventsRepository } from '../data/IEventsRepository';

dayjs.extend(utc);

type controllerParams = {
  eventsRepository: IEventsRepository,
  jwtSecret: string
}

export function initializeOnlineController({
  eventsRepository,
  jwtSecret,
}: controllerParams): Router {
  const router = Router();
  const jwt = jwtVerifyMiddleware(jwtSecret);

  const getOnlineSchema = {
    query: {
      pathname: Joi.string(),
    },
  };

  router.get('/', jwt, validation(getOnlineSchema), async (req: TypedRequest<typeof getOnlineSchema>, res, next) => {
    try {
      const {
        pathname,
      } = req.query;

      const online = await eventsRepository.countOnline({
        pathname,
      });
      return res.json({ online });
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
