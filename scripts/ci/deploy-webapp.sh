#!/usr/bin/env bash
set -euo pipefail
for name in ARTIFACT_DIR RESOURCE_GROUP WEBAPP_NAME SPRING_DATASOURCE_URL \
  SPRING_DATASOURCE_USERNAME SPRING_DATASOURCE_PASSWORD JWT_SECRET; do
  value="${!name:-}"
  if [[ -z "$value" || "$value" == *'$('* ]]; then
    echo "Variavel obrigatoria ausente: $name" >&2; exit 1
  fi
done
cd "$ARTIFACT_DIR"
sha256sum --check SHA256SUMS
build_id="$(cat build-id.txt)"
[[ "$build_id" =~ ^[0-9]+$ ]] || { echo 'Build ID invalido.' >&2; exit 1; }
# Somente a aplicacao e configurada/publicada. Nenhuma operacao sobre VM ou banco.
az webapp config appsettings set -g "$RESOURCE_GROUP" -n "$WEBAPP_NAME" \
  --settings "SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL" \
    "SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME" \
    "SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD" "JWT_SECRET=$JWT_SECRET" \
    "APP_BUILD_ID=$build_id" SPRING_JPA_HIBERNATE_DDL_AUTO=none SERVER_PORT=8080 \
  --only-show-errors --output none
az webapp deploy -g "$RESOURCE_GROUP" -n "$WEBAPP_NAME" --src-path app.jar \
  --type jar --async true --only-show-errors --output none
url="https://${WEBAPP_NAME}.azurewebsites.net"
ready=false
for ((attempt=0; attempt<120; attempt++)); do
  if response="$(curl --fail --silent --show-error --connect-timeout 5 --max-time 15 "$url/" 2>/dev/null)" \
     && [[ "$response" == *"build $build_id"* ]]; then
    ready=true; break
  fi
  sleep 5
done
[[ "$ready" == true ]] || { echo "Build $build_id nao ficou pronto no Web App." >&2; exit 1; }
bash scripts/smoke.sh "$url"
echo "Deploy confirmado: build $build_id em $url"
