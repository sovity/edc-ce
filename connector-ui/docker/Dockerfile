# Serve app with nginx
FROM docker.io/nginxinc/nginx-unprivileged:1.27-alpine3.20

# Temporarily switch to root to install packages and create symlink in restricted location
USER root
RUN apk add --no-cache jq curl

COPY ./dist/edc-ui /usr/share/nginx/html
COPY ./src/assets /usr/share/nginx/html/assets
COPY docker/default.conf.template etc/nginx/templates/default.conf.template
# Before starting nginx, apply ENV vars to create app-configuration.json from EDC_UI_* ENV Vars
# Use an entrypoint drop-in instead of modifying the default entrypoint or command,
# so that the automatic envsubst templating is not disabled.
COPY --chmod=755 --chown=root:root docker/99-generate-app-config.sh /docker-entrypoint.d/99-generate-app-config.sh

RUN ln -sf /tmp/app-configuration.json /usr/share/nginx/html/assets/config/app-configuration.json \
  # Nginx is configured to reject symlinks that point to a file owned by a different user, for security reasons
  && chown --no-dereference nginx:root /usr/share/nginx/html/assets/config/app-configuration.json

ARG EDC_UI_BUILD_VERSION_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_UI_BUILD_VERSION_ARG, so there's no git description."
ENV EDC_UI_BUILD_VERSION=$EDC_UI_BUILD_VERSION_ARG

ARG EDC_UI_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_UI_BUILD_DATE_ARG, so there's no build date."
ENV EDC_UI_BUILD_DATE=$EDC_UI_BUILD_DATE_ARG

# Switch back to unprivileged user for runtime
USER nginx:nginx

ENV NGINX_BIND="0.0.0.0"
ENV NGINX_PORT=8080
ENV NGINX_ACCESS_LOG=/dev/stdout
ENV NGINX_ERROR_LOG=/dev/stderr

HEALTHCHECK --interval=2s --timeout=5s --retries=10 \
  CMD curl -f http://$NGINX_BIND:$NGINX_PORT/ || exit 1
