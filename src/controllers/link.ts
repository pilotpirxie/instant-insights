import 'dotenv';
import { Router } from 'express';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import NodeCache from 'node-cache';
import UAParser from 'ua-parser-js';
import Joi from 'joi';
import { DataStorage } from '../storage/dataStorage';
import { TypedRequest } from '../types/express';
import Links from '../domain/links';

dayjs.extend(utc);

type controllerParams = {
  dataStorage: DataStorage,
  cacheStorage: NodeCache,
  jwtSecret: string,
  uaParser: UAParser
}

export function initializeLinkController({
  dataStorage,
  cacheStorage,
  uaParser,
}: controllerParams): Router {
  const router = Router();

  const getLinkSchema = {
    params: {
      linkName: Joi.string().required(),
      affiliate: Joi.string().max(64).optional().allow(''),
    },
  };

  router.get('/:linkName/:affiliate?', async (req: TypedRequest<typeof getLinkSchema>, res, next) => {
    try {
      const { ip } = req;
      if (!ip) return res.sendStatus(400);

      const userAgent = req.headers['user-agent'];
      if (!userAgent) return res.sendStatus(400);
      const ua = uaParser.setUA(userAgent).getResult();
      const os = ua.os.name;
      const osVersion = ua.os.version;

      const { linkName, affiliate } = req.params;

      let links: Links | undefined | null;

      if (cacheStorage.has(linkName)) {
        links = cacheStorage.get<Links>(linkName);
      } else {
        links = await dataStorage.getLinks(linkName);
        cacheStorage.set(linkName, links);
      }

      if (!links) return res.sendStatus(404);

      dataStorage.addLinkHit({
        name: linkName,
        affiliate: affiliate || '',
        meta: {
          ip,
          os: os || 'other',
          osVersion: osVersion || 'unknown',
        },
        params: {},
      }).catch((err) => {
        console.error('Failed to insert link hit', err);
      });
      if (os === 'iOS') return res.redirect(links.ios);
      if (os === 'Android') return res.redirect(links.android);
      return res.redirect(links.other);
    } catch (e) {
      return next(e);
    }
  });

  return router;
}
