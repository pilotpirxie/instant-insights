import "dotenv";
import { Router } from "express";
import Joi from "joi";
import validation from "../middlewares/validation";
import { TypedRequest } from "../types/express";
import { DataStorage } from "../storage/dataStorage";

export function initializeEventsController(dataStorage: DataStorage): Router {
  const router = Router();

  const addEventSchema = {
    body: {
      appId: Joi.number().required(),
      type: Joi.string().required(),
      pathname: Joi.string().required(),
      meta: Joi.object({
        os: Joi.string(),
        osVersion: Joi.string(),
        userId: Joi.string(),
        ipAddress: Joi.string(),
      }).required(),
      params: Joi.object().required(),
    },
  };

  router.post("/", validation(addEventSchema), async (req: TypedRequest<typeof addEventSchema>, res, next) => {
    try {
      const {
        appId, pathname, type, params, meta,
      } = req.body;

      dataStorage.addEvent({
        appId,
        pathname,
        params,
        meta,
        type,
      }).catch((err) => {
        console.error("Failed to insert event", err);
      });
      return res.sendStatus(200);
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
