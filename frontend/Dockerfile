FROM node:24-trixie-slim
WORKDIR /app

COPY public ./public
COPY .next/standalone .
COPY .next/static .next/static
COPY .env.prod .env

ARG NEXT_PUBLIC_BUILD_VERSION_ARG="The docker container was built outside of github actions and you didn't provide the build arg NEXT_PUBLIC_BUILD_VERSION_ARG."
ENV NEXT_PUBLIC_BUILD_VERSION=$NEXT_PUBLIC_BUILD_VERSION_ARG

ARG NEXT_PUBLIC_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg NEXT_PUBLIC_BUILD_DATE_ARG."
ENV NEXT_PUBLIC_BUILD_DATE=$NEXT_PUBLIC_BUILD_DATE_ARG

USER node

EXPOSE 8080

ENV PORT=8080
ENV HOSTNAME="0.0.0.0"

CMD ["node", "server.js"]
