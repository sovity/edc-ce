FROM node:18-alpine
WORKDIR /app

# Check https://github.com/nodejs/docker-node/tree/b4117f9333da4138b03a546ec926ef50a31506c3#nodealpine to understand why libc6-compat might be needed.
RUN apk add --no-cache libc6-compat


RUN addgroup -g 1001 -S nodejs
RUN adduser -S nextjs -u 1001

COPY --chown=nextjs:nodejs public ./public
COPY --chown=nextjs:nodejs .next/standalone .
COPY --chown=nextjs:nodejs .next/static .next/static
COPY --chown=nextjs:nodejs .env.prod .env

ARG NEXT_PUBLIC_BUILD_VERSION_ARG="The docker container was built outside of github actions and you didn't provide the build arg NEXT_PUBLIC_BUILD_VERSION_ARG."
ENV NEXT_PUBLIC_BUILD_VERSION=$NEXT_PUBLIC_BUILD_VERSION_ARG

ARG NEXT_PUBLIC_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg NEXT_PUBLIC_BUILD_DATE_ARG."
ENV NEXT_PUBLIC_BUILD_DATE=$NEXT_PUBLIC_BUILD_DATE_ARG

USER nextjs

EXPOSE 8080

ENV PORT=8080
ENV HOSTNAME=0.0.0.0

CMD node server.js
