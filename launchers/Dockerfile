FROM eclipse-temurin:17-jre-alpine

# Install curl for healthcheck, bash for entrypoint
RUN apk add --no-cache curl bash
SHELL ["/bin/bash", "-c"]

# Use a non-root user
RUN adduser -D -H -s /sbin/nologin edc
USER edc:edc

# Create empty properties file to avoid errors when loading properties
RUN touch /emtpy-properties-file.properties

# Which app.jar to include from build
ARG CONNECTOR_NAME="sovity-ce"

# For last-commit-info extension
ARG EDC_LAST_COMMIT_INFO_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_LAST_COMMIT_INFO_ARG, so there's no last commit info."
ARG EDC_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_BUILD_DATE_ARG, so there's no build date."

WORKDIR /app
COPY ./launchers/connectors/$CONNECTOR_NAME/build/libs/app.jar /app

# Copy logging.properties
COPY ./launchers/logging.properties /app

# Copy .env file. Replaces var statements so they don't overwrite existing env vars
COPY ./launchers/.env /app/.env
RUN sed -ri 's/^\s*(\S+)=(.*)$/\1=${\1:-"\2"}/' .env

ENV EDC_LAST_COMMIT_INFO=$EDC_LAST_COMMIT_INFO_ARG
ENV EDC_BUILD_DATE=$EDC_BUILD_DATE_ARG
ENV JVM_ARGS=""

ENTRYPOINT set -a && source /app/.env && set +a && exec java -Djava.util.logging.config.file=/app/logging.properties $JVM_ARGS -jar app.jar

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl --fail http://localhost:11001/api/check/health
