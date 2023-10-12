import { NextFunction, Request, Response } from 'express';
import jwt, { JwtPayload, Secret } from 'jsonwebtoken';

// eslint-disable-next-line max-len
export const jwtVerifyMiddleware = (jwtSecret: string) => (req: Request, res: Response, next: NextFunction) => {
  if (!req.headers.authorization || req.headers.authorization.split(' ')[0] !== 'Bearer') {
    return res.status(401).json({ message: 'Missing Authorization Header' });
  }

  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    return res.status(401).json({
      error: 'Unauthorized',
    });
  }

  try {
    jwt.verify(token, jwtSecret as Secret) as JwtPayload;
    // if (decoded.aud !== 'api') {
    //   return res.status(401).json({
    //     error: 'Unauthorized',
    //   });
    // }

    return next();
  } catch (err) {
    return res.status(401).json({
      error: 'Unauthorized',
    });
  }
};
