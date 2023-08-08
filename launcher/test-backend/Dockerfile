FROM gradle:7-jdk17-alpine AS build

ARG BUILD_ARGS

COPY --chown=gradle:gradle . /home/gradle/project/
WORKDIR /home/gradle/project/
RUN --mount=type=cache,target=/home/gradle/.gradle/caches gradle build --no-daemon $BUILD_ARGS

FROM eclipse-temurin:17-jre-alpine

# Install curl for healthcheck, bash for entrypoint
RUN apk add --no-cache curl bash
SHELL ["/bin/bash", "-c"]

# Create empty properties file to avoid errors when loading properties
RUN touch /emtpy-properties-file.properties

RUN adduser -D -H -s /sbin/nologin edc
USER edc:edc

ARG EDC_LAST_COMMIT_INFO_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_LAST_COMMIT_INFO_ARG, so there's no last commit info."
ENV EDC_LAST_COMMIT_INFO=$EDC_LAST_COMMIT_INFO_ARG

ARG EDC_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_BUILD_DATE_ARG, so there's no build date."
ENV EDC_BUILD_DATE=$EDC_BUILD_DATE_ARG

# Optional JVM arguments, such as memory settings
ARG JVM_ARGS=""

WORKDIR /app

COPY --from=build /home/gradle/project/launcher/test-backend/build/libs/app.jar /app
COPY ./launcher/test-backend/src/main/resources/logging.properties /app

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl --fail http://localhost:11001/api/check/health

# Use "exec" for graceful termination (SIGINT) to reach JVM.
# ARG can not be used in ENTRYPOINT so storing values in ENV variables
ENV JVM_ARGS=$JVM_ARGS

# Read ENV Vars from .env with substitution
COPY ./launcher/test-backend/.env /app/.env

# Replaces ENV Var statements so they don't overwrite existing ENV Vars
RUN sed -ri 's/^\s*(\S+)=(.*)$/\1=${\1:-"\2"}/' .env
ENTRYPOINT set -a && source /app/.env && set +a && exec java -Djava.util.logging.config.file=/app/logging.properties $JVM_ARGS -jar app.jar
