# Stage 1: Install node modules
FROM docker.io/library/node:lts as npm-install

WORKDIR /app
COPY ./package*.json /app/
RUN npm install

# Stage 2: Build Project
FROM docker.io/library/node:lts as build

WORKDIR /app
COPY --from=npm-install /app/node_modules /app/node_modules
COPY ./ /app/
RUN npm run ng build --no-progress --configuration=production

RUN ./docker/generate-csp-hash.mjs dist/edc-demo-client/*.js > script-src.txt

# Stage 3: Serve app with nginx
FROM docker.io/nginxinc/nginx-unprivileged:1.25-alpine3.18

# Temporarily switch to root to install packages and create symlink in restricted location
USER root
RUN apk add --no-cache jq curl

COPY --from=build /app/dist/edc-demo-client /usr/share/nginx/html
COPY --from=build /app/src/assets /usr/share/nginx/html/assets
COPY docker/default.conf.template etc/nginx/templates/default.conf.template
# Before starting nginx, apply ENV vars to create app-config.json from EDC_UI_* ENV Vars
# Use an entrypoint drop-in instead of modifying the default entrypoint or command,
# so that the automatic envsubst templating is not disabled.
COPY docker/99-generate-app-config.sh /docker-entrypoint.d/99-generate-app-config.sh

# Mount the template from the build context and the hash list from the previous stage
# instead of copying them, as they are not needed in the final image.
RUN --mount=type=bind,from=build,source=/app/script-src.txt,target=/tmp/script-src.txt \
  --mount=type=bind,source=/docker/headers.include.template,target=/tmp/headers.include.template \
  env SCRIPT_SRC_EXTRA="$(cat /tmp/script-src.txt)" \
  envsubst '$$SCRIPT_SRC_EXTRA' < /tmp/headers.include.template > /etc/nginx/headers.include

RUN ln -sf /tmp/app-config.json /usr/share/nginx/html/assets/config/app-config.json \
  # Nginx is configured to reject symlinks that point to a file owned by a different user, for security reasons
  && chown --no-dereference nginx:root /usr/share/nginx/html/assets/config/app-config.json

# Switch back to unprivileged user for runtime
USER nginx:nginx

ENV NGINX_BIND="0.0.0.0"
ENV NGINX_PORT=8080
ENV NGINX_ACCESS_LOG=/dev/stdout
ENV NGINX_ERROR_LOG=/dev/stderr

HEALTHCHECK --interval=2s --timeout=5s --retries=10 \
  CMD curl -f http://$NGINX_BIND:$NGINX_PORT/ || exit 1
