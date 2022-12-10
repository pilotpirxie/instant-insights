import 'dotenv';
import { Router } from 'express';
import Joi from 'joi';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import validation from '../middlewares/validation';
import { TypedRequest } from '../types/express';
import { DataStorage } from '../storage/dataStorage';

dayjs.extend(utc);

type controllerParams = {
  dataStorage: DataStorage,
}

export function initializeEventsController({ dataStorage }: controllerParams): Router {
  const router = Router();

  const addEventSchema = {
    query: {
      token: Joi.string().required(),
    },
    body: {
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
      const { token } = req.query;
      if (token !== process.env.API_WRITE_TOKEN) return res.sendStatus(401);

      const {
        pathname, type, params, meta, fingerprint,
      } = req.body;

      dataStorage.addEvent({
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
        pathname, type, limit, dateFrom, dateTo, fingerprint,
      } = req.query;

      const events = await dataStorage.getEvents({
        pathname,
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

  const getTypesSchema = {
    query: {
      dateFrom: Joi.string().required(),
      dateTo: Joi.string(),
    },
  };

  router.get('/types', validation(getTypesSchema), async (req: TypedRequest<typeof getTypesSchema>, res, next) => {
    try {
      const {
        dateTo, dateFrom,
      } = req.query;

      const events = await dataStorage.getTypes({
        dateFrom: dayjs(dateFrom).utc().toDate(),
        dateTo: dayjs(dateTo).utc().toDate(),
      });
      return res.json(events);
    } catch (e) {
      return next(e);
    }
  });

  const getSummarySchema = {};

  router.get('/summary', async (req: TypedRequest<typeof getSummarySchema>, res, next) => {
    try {
      const summary = await dataStorage.getSummary();
      return res.json(summary);
    } catch (e) {
      return next(e);
    }
  });

  const eventsExplorerSchema = {
    query: {
      dateFrom: Joi.string().required(),
      dateTo: Joi.string(),
      interval: Joi.string().allow('minute', 'hour', 'day', 'week', 'month', 'year').required(),
      os: Joi.string().allow('all', 'android', 'ios').optional(),
    },
  };

  router.get('/activity', async (req: TypedRequest<typeof eventsExplorerSchema>, res, next) => {
    try {
      if (req.query.interval === 'minute' && dayjs(req.query.dateTo).diff(dayjs(req.query.dateFrom), 'day') > 7) {
        return res.status(400).json({ error: 'Too big interval' });
      }

      if (req.query.interval === 'hour' && dayjs(req.query.dateTo).diff(dayjs(req.query.dateFrom), 'day') > 90) {
        return res.status(400).json({ error: 'Too big interval' });
      }

      const data = await dataStorage.getUsersActivity({
        dateFrom: dayjs(req.query.dateFrom).utc().toDate(),
        dateTo: dayjs(req.query.dateTo).utc().toDate(),
        interval: req.query.interval,
        os: req.query.os || 'all',
      });
      return res.json(data);
    } catch (e) {
      return next(e);
    }
  });

  router.get('/count', validation(eventsExplorerSchema), async (req: TypedRequest<typeof eventsExplorerSchema>, res, next) => {
    try {
      if (req.query.interval === 'minute' && dayjs(req.query.dateTo).diff(dayjs(req.query.dateFrom), 'day') > 7) {
        return res.status(400).json({ error: 'Too big interval' });
      }

      if (req.query.interval === 'hour' && dayjs(req.query.dateTo).diff(dayjs(req.query.dateFrom), 'day') > 90) {
        return res.status(400).json({ error: 'Too big interval' });
      }

      const data = await dataStorage.getEventsCount({
        dateFrom: dayjs(req.query.dateFrom).utc().toDate(),
        dateTo: dayjs(req.query.dateTo).utc().toDate(),
        interval: req.query.interval,
        os: req.query.os || 'all',
      });
      return res.json(data);
    } catch (e) {
      return next(e);
    }
  });

  router.get('/pathnames', async (req: TypedRequest<typeof eventsExplorerSchema>, res, next) => {
    try {
      const data = await dataStorage.getPathnamesPopularity({
        dateFrom: dayjs(req.query.dateFrom).utc().toDate(),
        dateTo: dayjs(req.query.dateTo).utc().toDate(),
        os: req.query.os || 'all',
      });
      return res.json(data);
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
