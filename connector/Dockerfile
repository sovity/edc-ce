FROM gradle:7-jdk17-alpine AS build

ARG USERNAME
ARG TOKEN
ARG BUILD_ARGS
ARG TEST_POSTGRES_JDBC_URL
ARG TEST_POSTGRES_JDBC_USER
ARG TEST_POSTGRES_JDBC_PASSWORD

ENV USERNAME=$USERNAME
ENV TOKEN=$TOKEN

ENV SKIP_TESTCONTAINERS=true
ENV TEST_POSTGRES_JDBC_URL=$TEST_POSTGRES_JDBC_URL
ENV TEST_POSTGRES_JDBC_USER=$TEST_POSTGRES_JDBC_USER
ENV TEST_POSTGRES_JDBC_PASSWORD=$TEST_POSTGRES_JDBC_PASSWORD

COPY --chown=gradle:gradle . /home/gradle/project/
WORKDIR /home/gradle/project/
RUN --mount=type=cache,target=/home/gradle/.gradle/caches gradle build --no-daemon $BUILD_ARGS

FROM eclipse-temurin:17-jre-alpine

# Optional JVM arguments, such as memory settings
ARG JVM_ARGS=""

# Install curl for healthcheck and create an empty properties file as migitation for a core EDC issue
RUN apk add --no-cache curl && touch /emtpy-properties-file.properties

WORKDIR /app

COPY --from=build /home/gradle/project/connector/build/libs/app.jar /app
COPY ./connector/src/main/resources/logging.properties /app

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl -H "X-Api-Key: $EDC_API_AUTH_KEY" --fail http://localhost:11002/backend/api/v1/management/check/health

# Use "exec" for graceful termination (SIGINT) to reach JVM.
# ARG can not be used in ENTRYPOINT so storing values in ENV variables
ENV JVM_ARGS=$JVM_ARGS

# Read ENV Vars from .env with substitution
COPY ./connector/.env /app/.env

# Replaces ENV Var statements so they don't overwrite existing ENV Vars
RUN sed -ri 's/^\s*(\S+)=(.*)$/\1=${\1:-"\2"}/' .env
ENTRYPOINT bash -c 'set -a && source /app/.env && set +a && exec java -Djava.util.logging.config.file=/app/logging.properties $JVM_ARGS -jar app.jar'
