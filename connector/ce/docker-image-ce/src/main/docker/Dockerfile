FROM eclipse-temurin:21-jre-alpine

# the entrypoint requires bash
RUN apk add --no-cache bash
SHELL ["/bin/bash", "-c"]

# Use a non-root user
RUN adduser -D -H -s /sbin/nologin edc
USER edc:edc

WORKDIR /app

COPY --chown=edc:edc --chmod=644 ./build/libs/app.jar /app

ARG JAVA_ARGS
ENV JAVA_ARGS=$JAVA_ARGS

ARG SOVITY_BUILD_VERSION_ARG="The docker container was built outside of github actions and you didn't provide the build arg SOVITY_BUILD_VERSION_ARG, so there's no last commit info."
ENV SOVITY_BUILD_VERSION=$SOVITY_BUILD_VERSION_ARG

ARG SOVITY_BUILD_DATE_ARG="The docker container was built outside of github actions and you didn't provide the build arg SOVITY_BUILD_DATE_ARG, so there's no build date."
ENV SOVITY_BUILD_DATE=$SOVITY_BUILD_DATE_ARG

COPY --chown=edc:edc --chmod=644 ./src/main/docker/log4j2-console-DEBUG.xml /app/log4j2-console-DEBUG.xml
COPY --chown=edc:edc --chmod=644 ./src/main/docker/log4j2-console-INFO.xml /app/log4j2-console-INFO.xml
COPY --chown=edc:edc --chmod=644 ./src/main/docker/log4j2-json-DEBUG.xml /app/log4j2-json-DEBUG.xml
COPY --chown=edc:edc --chmod=644 ./src/main/docker/log4j2-json-INFO.xml /app/log4j2-json-INFO.xml
COPY --chown=edc:edc --chmod=754 ./src/main/docker/docker-entrypoint.sh /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["start"]
