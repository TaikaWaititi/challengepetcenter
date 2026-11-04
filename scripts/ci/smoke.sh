#!/usr/bin/env bash
set -euo pipefail
base_url="${1:?Informe a URL base da aplicacao}"
curl --fail --silent --show-error --retry 40 --retry-delay 5 --retry-all-errors \
  --connect-timeout 5 --max-time 10 "$base_url/" >/dev/null
curl --fail --silent --show-error --retry 10 --retry-delay 5 --retry-all-errors \
  --connect-timeout 5 --max-time 15 "$base_url/api-docs" >/dev/null
echo 'Smoke HTTP da API e OpenAPI aprovado; CRUD e SELECT devem ser evidenciados no video.'
