#!/bin/bash
set -euo pipefail
export VSO_AGENT_IGNORE=AZP_TOKEN,AZP_TOKEN_FILE
if [[ ! -f .agent ]]; then
  ./config.sh --unattended --url "$AZP_URL" --auth PAT --token "$(cat /run/secrets/registration-token)" --pool "$AZP_POOL" --agent "$AZP_AGENT_NAME" --work _work --acceptTeeEula
fi
exec ./run.sh
