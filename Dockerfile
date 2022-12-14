FROM node:18-alpine

WORKDIR /app

COPY . .

RUN yarn
RUN yarn build

ADD ./src/migrations dist/migrations
ADD ./public dist/public

ENV PORT=3000

CMD ["yarn", "run:js"]

EXPOSE 3000
