# Checklist DevOps - Challenge PetCenter

## Itens ja preparados

- [x] Dockerfile da API Java.
- [x] Docker Compose com API e H2 em containers separados.
- [x] Banco H2 containerizado com imagem `oscarfonts/h2`.
- [x] Volume nomeado `h2-data` para persistencia.
- [x] API rodando em background com `docker compose up -d`.
- [x] API rodando com usuario nao-root `spring`.
- [x] Portas configuradas: `8080` para API e `81` para console H2.
- [x] Script Azure CLI em `DevOps.sh`.
- [x] Script provisiona VM Linux.
- [x] Script abre portas necessarias.
- [x] Script instala Docker, Docker Compose, Git, nano e unzip.
- [x] Script cria usuario `appuser` na VM.
- [x] Script clona o repositorio e sobe containers.
- [x] README com descricao do projeto.
- [x] README com beneficios para o negocio.
- [x] README com arquitetura macro.
- [x] README com rotas.
- [x] README com how-to de instalacao.
- [x] Teste local de CRUD.
- [x] Teste local de persistencia.
- [x] Script local de teste em `scripts/test-devops-local.ps1`.
- [x] Imagem Docker otimizada para aproximadamente `234MB`, abaixo de `400MB`.

## Itens que dependem da etapa Azure/video

- [ ] Subir repositorio publico no GitHub.
- [ ] Alterar `GITHUB_REPO_URL` no `DevOps.sh`.
- [ ] Executar `DevOps.sh` no Azure Cloud Shell.
- [ ] Testar API pelo IP publico da VM.
- [ ] Gravar video narrado em 720p ou superior.
- [ ] Publicar video no YouTube como nao listado.
- [ ] Inserir link do GitHub no PDF final.
- [ ] Inserir link do YouTube no PDF final.
- [ ] Tirar print da exclusao da VM/Resource Group.
- [ ] Inserir print da exclusao no PDF final.
