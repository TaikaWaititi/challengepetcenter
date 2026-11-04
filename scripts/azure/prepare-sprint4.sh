#!/usr/bin/env bash
# Executar manualmente apos revisao. Cria somente ACR e identidade, nunca banco.
set -euo pipefail
: "${RESOURCE_GROUP:?Informe o grupo de recursos da Sprint 4}"
: "${AZURE_LOCATION:?Informe a regiao Azure}"
: "${ACR_NAME:?Informe um nome globalmente unico para o ACR}"
: "${ACI_IDENTITY_NAME:?Informe o nome da identidade de pull}"
az group create -n "$RESOURCE_GROUP" -l "$AZURE_LOCATION" --output none
if ! az acr show -n "$ACR_NAME" -g "$RESOURCE_GROUP" --output none 2>/dev/null; then
  az acr create -n "$ACR_NAME" -g "$RESOURCE_GROUP" --sku Basic --admin-enabled false --output none
fi
az identity create -n "$ACI_IDENTITY_NAME" -g "$RESOURCE_GROUP" --output none
acr_id="$(az acr show -n "$ACR_NAME" -g "$RESOURCE_GROUP" --query id -o tsv)"
principal="$(az identity show -n "$ACI_IDENTITY_NAME" -g "$RESOURCE_GROUP" --query principalId -o tsv)"
az role assignment create --assignee-object-id "$principal" --assignee-principal-type ServicePrincipal \
  --role AcrPull --scope "$acr_id" --output none
az identity show -n "$ACI_IDENTITY_NAME" -g "$RESOURCE_GROUP" --query '{ACI_IDENTITY_ID:id}' -o json
echo 'Configure a service connection e o banco em nuvem conforme docs/SPRINT4.md.'
