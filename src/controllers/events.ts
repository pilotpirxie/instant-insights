import 'dotenv';
import { Router } from 'express';
import Joi from 'joi';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import validation from '../middlewares/validation';
import { TypedRequest } from '../types/express';
import { DataStorage } from '../storage/dataStorage';

dayjs.extend(utc);

export function initializeEventsController(dataStorage: DataStorage): Router {
  const router = Router();

  const addEventSchema = {
    body: {
      appId: Joi.number().required(),
      type: Joi.string().required(),
      pathname: Joi.string().required(),
      fingerprint: Joi.string().required(),
      meta: Joi.object({
        os: Joi.string(),
        osVersion: Joi.string(),
        userId: Joi.string(),
        ipAddress: Joi.string(),
      }).required(),
      params: Joi.object().required(),
    },
  };

  router.post('/', validation(addEventSchema), async (req: TypedRequest<typeof addEventSchema>, res, next) => {
    try {
      const {
        appId, pathname, type, params, meta, fingerprint,
      } = req.body;

      dataStorage.addEvent({
        appId,
        pathname,
        params,
        meta,
        type,
        fingerprint,
      }).catch((err) => {
        console.error('Failed to insert event', err);
      });
      return res.sendStatus(200);
    } catch (e) {
      return next(e);
    }
  });

  const getEventSchema = {
    query: {
      appId: Joi.number().required(),
      type: Joi.string(),
      pathname: Joi.string(),
      fingerprint: Joi.string(),
      limit: Joi.number().min(1).max(500000).required(),
      dateFrom: Joi.string().required(),
      dateTo: Joi.string(),
    },
  };

  router.get('/', validation(getEventSchema), async (req: TypedRequest<typeof getEventSchema>, res, next) => {
    try {
      const {
        pathname, type, appId, limit, dateFrom, dateTo, fingerprint,
      } = req.query;

      const events = await dataStorage.getEvents({
        pathname,
        appId,
        limit,
        type,
        fingerprint,
        dateFrom: dayjs(dateFrom).utc().toDate(),
        dateTo: dayjs(dateTo).utc().toDate(),
      });
      return res.json(events);
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
