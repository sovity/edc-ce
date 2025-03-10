#!/usr/bin/env bash
# Use bash instead of sh,
# because sh in this image is provided by dash (https://git.kernel.org/pub/scm/utils/dash/dash.git/),
# which seems to eat environment variables containing dashes,
# which are required for some EDC configuration values.

# Do not set -u to permit unset variables in .env
set -eo pipefail

require_one_of() {
    local prop_name="$1"
    local prop_value="$2"
    shift
    shift
    local allowed_values=("$@")

    # Check if prop_value matches any of the allowed values
    for value in "${allowed_values[@]}"; do
        if [ "$prop_value" = "$value" ]; then
            return 0  # Match found
        fi
    done

    # If no match found, print error message and exit
    echo "Invalid Value: $prop_name=$prop_value Expected: ${allowed_values[*]}" >&2
    exit 1
}

if [[ "x${1:-}" == "xstart" ]]; then
    cmd=(java ${JAVA_ARGS:-})

    REMOTE_DEBUG=${REMOTE_DEBUG:-"false"}
    REMOTE_DEBUG_SUSPEND=${REMOTE_DEBUG_SUSPEND:-"false"}
    REMOTE_DEBUG_BIND=${REMOTE_DEBUG_BIND:-"127.0.0.1:5005"}
    LOGGING_LEVEL=${LOGGING_LEVEL:-"INFO"}
    LOGGING_KIND=${LOGGING_KIND:-"console"}

    # These are also documented in the RuntimeModule
    # Please keep them in sync
    require_one_of "REMOTE_DEBUG" "$REMOTE_DEBUG" "true" "false"
    require_one_of "REMOTE_DEBUG_SUSPEND" "$REMOTE_DEBUG_SUSPEND" "true" "false"

    # These are also documented in the RuntimeModule
    # Please keep them in sync
    require_one_of "LOGGING_LEVEL" "$LOGGING_LEVEL" "INFO" "DEBUG"
    require_one_of "LOGGING_KIND" "$LOGGING_KIND" "console" "json"

    if [ "$REMOTE_DEBUG" = "true" ]; then
        debug_cmd="-agentlib:jdwp=transport=dt_socket,server=y"
        if [ "$REMOTE_DEBUG_SUSPEND" = "true" ]; then
          debug_cmd+=",suspend=y"
        else
          debug_cmd+=",suspend=n"
        fi
        debug_cmd+=",address=$REMOTE_DEBUG_BIND"

        cmd+=(
            "$debug_cmd"
        )
    fi

    logging_config="/app/log4j2-$LOGGING_KIND-$LOGGING_LEVEL.xml"

    cmd+=(
        -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
        -Dlog4j2.configurationFile=${logging_config}
        -jar /app/app.jar
    )
else
    cmd=("$@")
fi

if [ "${REMOTE_DEBUG}" = "true" ]; then
  echo "Jar CMD: ${cmd[@]}"
fi

# Use "exec" for termination signals to reach JVM
exec "${cmd[@]}"
