#!/usr/bin/env bash
# Use bash instead of sh,
# because sh in this image is provided by dash (https://git.kernel.org/pub/scm/utils/dash/dash.git/),
# which seems to eat environment variables containing dashes,
# which are required for some EDC configuration values.

# Do not set -u to permit unset variables in .env
set -eo pipefail

# Apply ENV Vars on JAR startup
set -a
source /app/.env
set +a


if [[ "x${1:-}" == "xstart" ]]; then
    cmd=(java ${JAVA_ARGS:-})

    if [ "${REMOTE_DEBUG:-n}" = "y" ] || [ "${REMOTE_DEBUG:-false}" = "true" ]; then
        cmd+=(
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=${REMOTE_DEBUG_SUSPEND:-n},address=${REMOTE_DEBUG_BIND:-127.0.0.1:5005}"
        )
    fi

    logging_config='/app/logging.properties'
    if [ "${DEBUG_LOGGING:-n}" = "y" ] || [ "${DEBUG_LOGGING:-false}" = "true" ]; then
        logging_config='/app/logging.dev.properties'
    fi

    cmd+=(
        -Djava.util.logging.config.file=${logging_config}
        -jar /app/app.jar
    )
else
    cmd=("$@")
fi

if [ "${REMOTE_DEBUG:-n}" = "y" ] || [ "${REMOTE_DEBUG:-false}" = "true" ]; then
  echo "Jar CMD (printing, because REMOTE_DEBUG=y|true): ${cmd[@]}"
fi

# Use "exec" for termination signals to reach JVM
exec "${cmd[@]}"
