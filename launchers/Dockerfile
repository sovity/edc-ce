FROM eclipse-temurin:17-jre-alpine

# Install curl for healthcheck, bash for entrypoint
RUN apk add --no-cache curl bash
SHELL ["/bin/bash", "-c"]

# Use a non-root user
RUN adduser -D -H -s /sbin/nologin edc
USER edc:edc

# Which app.jar to include
ARG CONNECTOR_NAME="sovity-ce"

# For last-commit-info extension
ARG EDC_LAST_COMMIT_INFO_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_LAST_COMMIT_INFO_ARG, so there's no last commit info."
ARG EDC_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_BUILD_DATE_ARG, so there's no build date."

WORKDIR /app
COPY ./launchers/connectors/$CONNECTOR_NAME/build/libs/app.jar /app
COPY ./launchers/logging.properties /app
COPY ./launchers/logging.dev.properties /app
COPY ./launchers/.env /app/.env
RUN touch /app/emtpy-properties-file.properties

# Replaces var statements so when they are sourced as bash they don't overwrite existing env vars
RUN sed -ri 's/^\s*(\S+)=(.*)$/\1=${\1:-"\2"}/' .env

ENV EDC_LAST_COMMIT_INFO=$EDC_LAST_COMMIT_INFO_ARG
ENV EDC_BUILD_DATE=$EDC_BUILD_DATE_ARG
ENV JVM_ARGS=""

COPY ./launchers/docker-entrypoint.sh /app/entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["start"]

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl --fail http://localhost:11001/api/check/health
