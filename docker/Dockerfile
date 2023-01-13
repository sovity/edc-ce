# Build Arg: Angular Config Profile
ARG EDC_UI_CONFIGURATION=production

# Stage 1: Install node modules
FROM node:lts as npm-install

WORKDIR /app
COPY ./package*.json /app/
RUN npm install

# Stage 2: Build Project
FROM node:lts as build

WORKDIR /app
COPY --from=npm-install /app/node_modules /app/node_modules
COPY ./ /app/
RUN npm run ng build --no-progress --configuration=${EDC_UI_CONFIGURATION}

# Stage 3: Serve app with nginx
FROM nginx:1.23.2-alpine

COPY --from=build /app/dist/edc-demo-client /usr/share/nginx/html
COPY --from=build /app/src/assets /usr/share/nginx/html/assets
COPY docker/templates/default.conf.template /etc/nginx/templates/

ENV NGINX_BIND=127.0.0.1
ENV NGINX_PORT=80

HEALTHCHECK --interval=2s --timeout=5s --retries=10 \
  CMD curl -f http://localhost:$NGINX_PORT/ || exit 1
