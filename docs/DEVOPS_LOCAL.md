# Desenvolvimento local

Siga a secao de execucao local do [README](../README.md). Gere `.env` com `scripts/new-local-env.ps1` antes de subir o Compose. As senhas fixas antigas foram removidas.

Para validar o CI completo sem usar os containers ja existentes, execute `bash scripts/ci/test-oracle.sh` com Java 21 e Docker. O banco criado para esse teste e descartavel.

A execucao local serve ao desenvolvimento; a entrega da Sprint 4 precisa ser demonstrada na Azure.
