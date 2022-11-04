import 'dotenv';
import { Router } from 'express';
import Joi from 'joi';
import validation from '../middlewares/validation';
import { TypedRequest } from '../types/express';

export function initializeEventsController(): Router {
  const router = Router();

  const addEventSchema = {
    body: {
      appId: Joi.string().required(),
      type: Joi.string().required(),
      user: Joi.object({
        os: Joi.string(),
        osVersion: Joi.string(),
        userId: Joi.string(),
      }).required(),
      params: Joi.object().required(),
    },
  };

  router.post('/', validation(addEventSchema), async (req: TypedRequest<typeof addEventSchema>, res, next) => {
    try {
      const { params } = req.body;
      return res.sendStatus(200);
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
