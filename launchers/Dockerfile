FROM eclipse-temurin:17-jre-alpine

# Install curl for healthcheck, bash for entrypoint
RUN apk add --no-cache curl bash
SHELL ["/bin/bash", "-c"]

# Use a non-root user
RUN adduser -D -H -s /sbin/nologin edc
USER edc:edc

# Which app.jar to include
ARG CONNECTOR_NAME="sovity-ce"
ARG CONNECTOR_TYPE="connector"

# For last-commit-info extension
ARG EDC_LAST_COMMIT_INFO_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_LAST_COMMIT_INFO_ARG, so there's no last commit info."
ARG EDC_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg EDC_BUILD_DATE_ARG, so there's no build date."

WORKDIR /app
COPY ./launchers/connectors/$CONNECTOR_NAME/build/libs/app.jar /app
COPY ./launchers/logging.properties /app
COPY ./launchers/logging.dev.properties /app

RUN touch /app/empty-properties-file.properties

ENV EDC_LAST_COMMIT_INFO=$EDC_LAST_COMMIT_INFO_ARG
ENV EDC_BUILD_DATE=$EDC_BUILD_DATE_ARG
ENV JVM_ARGS=""

COPY ./launchers/docker-entrypoint.sh /app/entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["start"]

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl -H "x-api-key: $EDC_API_AUTH_KEY" --fail http://localhost:11001/api/check/health || curl -H "x-api-key: $EDC_API_AUTH_KEY" --fail http://localhost:11001/backend/api/check/health
