FROM node:22-bookworm-slim
WORKDIR /app

COPY public ./public
COPY .next/standalone .
COPY .next/static .next/static

USER node

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME=0.0.0.0

CMD ["node", "server.js"]
