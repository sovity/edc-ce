# Stage 1: Compile and Build angular codebase
FROM node:lts as build

WORKDIR /app
COPY ./ /app/
RUN npm install
RUN npm run ng build --prod

# Stage 2: Serve app with nginx
FROM nginx:1.23.2-alpine

COPY --from=build /app/dist/edc-demo-client /usr/share/nginx/html
COPY --from=build /app/src/assets /usr/share/nginx/html/assets
COPY docker/templates/default.conf.template /etc/nginx/templates/

ENV NGINX_BIND=127.0.0.1
ENV NGINX_PORT=80

HEALTHCHECK --interval=2s --timeout=5s --retries=10 \
  CMD curl -f http://localhost:$NGINX_PORT/ || exit 1
