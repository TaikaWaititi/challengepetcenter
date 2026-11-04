#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
# Credenciais descartaveis, diferentes a cada CI. Nao usa o banco da entrega.
export ORACLE_PASSWORD="$(openssl rand -hex 24)"
export APP_USER=PETCENTER
export APP_USER_PASSWORD="$(openssl rand -hex 24)"
export JWT_SECRET="$(openssl rand -base64 48)"
if [[ "${TF_BUILD:-}" == True ]]; then
  for secret in "$ORACLE_PASSWORD" "$APP_USER_PASSWORD" "$JWT_SECRET"; do
    printf '##vso[task.setsecret]%s\n' "$secret"
  done
fi
container_id=''
cleanup() {
  if [[ -n "$container_id" ]]; then docker rm -f "$container_id" >/dev/null; fi
}
trap cleanup EXIT
docker build -t petcenter-oracle-ci ./database
container_id="$(docker run -d -p 127.0.0.1::1521 \
  --env ORACLE_PASSWORD --env APP_USER --env APP_USER_PASSWORD \
  --health-cmd healthcheck.sh --health-interval 5s --health-timeout 10s \
  --health-retries 120 --health-start-period 60s petcenter-oracle-ci)"
ready=false
for ((attempt=0; attempt<180; attempt++)); do
  state="$(docker inspect --format '{{.State.Health.Status}}' "$container_id")"
  if [[ "$state" == healthy ]]; then ready=true; break; fi
  if [[ "$state" == unhealthy ]]; then break; fi
  sleep 5
done
if [[ "$ready" != true ]]; then
  echo 'Oracle de testes nao ficou saudavel; interrompendo o CI.' >&2
  exit 1
fi
port="$(docker port "$container_id" 1521/tcp | sed 's/.*://')"
export SPRING_DATASOURCE_URL="jdbc:oracle:thin:@localhost:${port}/FREEPDB1"
export SPRING_DATASOURCE_USERNAME="$APP_USER"
export SPRING_DATASOURCE_PASSWORD="$APP_USER_PASSWORD"
export SPRING_JPA_HIBERNATE_DDL_AUTO=none
bash ./mvnw -B -ntp clean verify -Poracle-it
