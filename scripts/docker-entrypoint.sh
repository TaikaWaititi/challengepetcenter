#!/bin/sh
set -eu

if [ -n "${WAIT_FOR_HOST:-}" ]; then
  WAIT_FOR_PORT="${WAIT_FOR_PORT:-1521}"
  WAIT_FOR_TIMEOUT="${WAIT_FOR_TIMEOUT:-900}"
  elapsed=0

  echo "Waiting for ${WAIT_FOR_HOST}:${WAIT_FOR_PORT}..."
  until nc -z "$WAIT_FOR_HOST" "$WAIT_FOR_PORT"; do
    if [ "$elapsed" -ge "$WAIT_FOR_TIMEOUT" ]; then
      echo "Timeout waiting for ${WAIT_FOR_HOST}:${WAIT_FOR_PORT}"
      exit 1
    fi

    sleep 5
    elapsed=$((elapsed + 5))
  done

  echo "${WAIT_FOR_HOST}:${WAIT_FOR_PORT} is reachable."
fi

exec java -jar /app/app.jar
