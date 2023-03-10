FROM gradle:7.6.0-jdk17 AS build

# Github pipeline sets this to the last commit information.
# This build arg is later converted to an ENV variable, since the github action didn't support ENVs we used build args.
ARG EDC_LAST_COMMIT_INFO_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_LAST_COMMIT_INFO_ARG, so there's no last commit info."
ARG BUILD_ARGS

COPY --chown=gradle:gradle . /home/gradle/project/
WORKDIR /home/gradle/project/
RUN gradle build --no-daemon $BUILD_ARGS

# -buster is required to have apt available
FROM openjdk:17-slim-buster

# Optional JVM arguments, such as memory settings
ARG JVM_ARGS=""

# Install curl, then delete apt indexes to save image space
RUN apt update \
    && apt install -y curl \
    && rm -rf /var/cache/apt/archives /var/lib/apt/lists

WORKDIR /app

COPY --from=build /home/gradle/project/connector/build/libs/app.jar /app
COPY ./connector/src/main/resources/logging.properties /app

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl -H "X-Api-Key: $EDC_API_AUTH_KEY" --fail http://localhost:9191/api/v1/data/check/health

ENV EDC_LAST_COMMIT_INFO=$EDC_LAST_COMMIT_INFO_ARG

# Use "exec" for graceful termination (SIGINT) to reach JVM.
# ARG can not be used in ENTRYPOINT so storing values in ENV variables
ENV JVM_ARGS=$JVM_ARGS

# Read ENV Vars from .env with substitution
COPY ./connector/.env /app/.env

# Replaces ENV Var statements so they don't overwrite existing ENV Vars
RUN sed -ri 's/^\s*(\S+)=(.*)$/\1=${\1:-"\2"}/' .env
ENTRYPOINT bash -c 'set -a && source /app/.env && set +a && exec java -Djava.util.logging.config.file=/app/logging.properties $JVM_ARGS -jar app.jar'
