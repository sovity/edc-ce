FROM gradle:8.0.0-jdk17 AS build

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

EXPOSE 8181
EXPOSE 9191
EXPOSE 8282

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl -H "X-Api-Key: $EDC_API_AUTH_KEY" --fail http://localhost:8181/api/check/health

ENV WEB_HTTP_PORT="8181"
ENV WEB_HTTP_PATH="/api"
ENV WEB_HTTP_DATA_PORT="9191"
ENV WEB_HTTP_DATA_PATH="/api/v1/data"
ENV WEB_HTTP_IDS_PORT="8282"
ENV WEB_HTTP_IDS_PATH="/api/v1/ids"
ENV EDC_LAST_COMMIT_INFO=$EDC_LAST_COMMIT_INFO_ARG

# Use "exec" for graceful termination (SIGINT) to reach JVM.
# ARG can not be used in ENTRYPOINT so storing values in ENV variables
ENV JVM_ARGS=$JVM_ARGS
ENTRYPOINT [ "sh", "-c", \
    "exec java -Djava.util.logging.config.file=/app/logging.properties $JVM_ARGS -jar app.jar"]
