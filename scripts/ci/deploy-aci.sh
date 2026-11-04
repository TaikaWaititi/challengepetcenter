#!/usr/bin/env bash
set -euo pipefail
for name in ARTIFACT_DIR RESOURCE_GROUP AZURE_LOCATION ACR_NAME ACI_NAME DNS_LABEL ACI_IDENTITY_ID \
  SPRING_DATASOURCE_URL SPRING_DATASOURCE_USERNAME SPRING_DATASOURCE_PASSWORD JWT_SECRET; do
  value="${!name:-}"
  if [[ -z "$value" || "$value" == *'$('* ]]; then
    echo "Variavel obrigatoria ausente: $name" >&2; exit 1
  fi
done
[[ "$ACI_NAME" =~ ^[a-z][a-z0-9-]{0,61}[a-z0-9]$ ]] || { echo 'ACI_NAME invalido.' >&2; exit 1; }
# Nunca redeployar o grupo legado que contem o Oracle sem volume persistente.
existing="$(az container list -g "$RESOURCE_GROUP" --query "[?name=='${ACI_NAME}'].containers[].name" -o tsv)"
if [[ -n "$existing" && "$existing" != "$ACI_NAME" ]]; then
  echo 'Destino existente nao e o grupo exclusivo da API da Sprint 4. Deploy bloqueado.' >&2
  exit 1
fi
if [[ "$SPRING_DATASOURCE_URL" == *'@localhost'* || "$SPRING_DATASOURCE_URL" == *'@oracle:'* \
   || "$SPRING_DATASOURCE_URL" == *'@127.0.0.1'* ]]; then
  echo 'Informe um Oracle em nuvem separado e acessivel pelo ACI.' >&2; exit 1
fi
cd "$ARTIFACT_DIR"
sha256sum --check SHA256SUMS
build_id="$(cat build-id.txt)"
[[ "$build_id" =~ ^[0-9]+$ ]] || { echo 'Build ID invalido.' >&2; exit 1; }
registry="$(az acr show -n "$ACR_NAME" --query loginServer -o tsv)"
image="$registry/petcenter-api:$build_id"
az acr login -n "$ACR_NAME" --only-show-errors >/dev/null
docker build --pull -t "$image" .
docker push "$image"
az container create -g "$RESOURCE_GROUP" -n "$ACI_NAME" --location "$AZURE_LOCATION" \
  --image "$image" --os-type Linux --cpu 1 --memory 1.5 --restart-policy Always \
  --assign-identity "$ACI_IDENTITY_ID" --acr-identity "$ACI_IDENTITY_ID" \
  --registry-login-server "$registry" --ip-address Public --dns-name-label "$DNS_LABEL" --ports 8080 \
  --environment-variables SPRING_JPA_HIBERNATE_DDL_AUTO=none \
  --secure-environment-variables "SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL" \
    "SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME" \
    "SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD" "JWT_SECRET=$JWT_SECRET" \
  --only-show-errors --output none
fqdn="$(az container show -g "$RESOURCE_GROUP" -n "$ACI_NAME" --query ipAddress.fqdn -o tsv)"
bash scripts/smoke.sh "http://$fqdn:8080"
echo "Deploy do build $build_id concluido: http://$fqdn:8080"
