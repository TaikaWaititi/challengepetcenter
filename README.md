# Challenge PetCenter - Sprint 4 DevOps

API REST para acompanhar saude, humor e rotina dos pets. Centraliza o historico para tutores e clinicas e apoia a continuidade do cuidado veterinario.

Stack: Java 21, Spring Boot 4.0.6, Maven Wrapper, Spring MVC, JPA/Hibernate, Bean Validation, Spring Security, JWT, Oracle JDBC, Oracle Database Free e Swagger/OpenAPI. Entrega DevOps: Azure Pipelines, Docker, Azure Container Registry (ACR), Azure Container Instances (ACI), Azure CLI e identidade gerenciada para pull no ACR.

## Estado desta versao

Versao candidata a entrega, com publicacao no GitHub autorizada pelo grupo. Oito testes unitarios e um teste de integracao com Oracle aprovados localmente. Em 04/11/2026 foi realizada uma publicacao manual de teste em ACI: API, login JWT e CRUD de pets e diarios aprovados com SELECTs no Oracle da Azure. A execucao real das pipelines e o deploy automatico da Sprint 4 continuam pendentes. Consulte [o passo a passo e as evidencias do teste em nuvem](docs/TESTE_NUVEM.md).

- [Configuracao e roteiro completo da Sprint 4](docs/SPRINT4.md)
- [Alteracoes, razoes e requisitos correspondentes do PDF](docs/ALTERACOES_SPRINT4.md)
- [Arquitetura proposta e fluxo CI/CD numerado](docs/arquitetura-sprint4.svg)

## Ambiente atual na Azure

Versao candidata a entrega publicada no grupo `rg-petcenter-rm564939-sprint4`, ACI `aci-petcenter-rm564939-sprint4`. API: http://petcenter-rm564939-sprint4.brazilsouth.azurecontainer.io:8080/ . Swagger: http://petcenter-rm564939-sprint4.brazilsouth.azurecontainer.io:8080/swagger-ui.html .

O banco foi preservado por exportacao/restauracao Data Pump e o CRUD foi aprovado novamente no novo endereco. A origem com nome de teste foi desativada. Consulte [a migracao e o backup](docs/MIGRACAO_NOMES.md). CI/CD remoto e persistencia duravel do Oracle continuam pendentes; o novo nome nao altera essas limitacoes.

## Arquitetura da Sprint 4

![Arquitetura proposta e fluxo CI/CD](docs/arquitetura-sprint4.svg)

O desenvolvedor envia codigo ao GitHub na branch `master`. O Azure DevOps compila e executa testes unitarios e de integracao contra Oracle descartavel, publica o JAR como Pipeline Artifact e dispara o CD. O CD baixa esse artefato, verifica o checksum, cria uma imagem com tag do build, publica no ACR e atualiza um ACI exclusivo da API. O usuario acessa API/Swagger; a API usa um Oracle em nuvem separado do ciclo de deploy da aplicacao.

**Banco do CD definitivo: infraestrutura separada ainda pendente.** Oracle da FIAP ou Oracle em infraestrutura persistente sao opcoes previstas no PDF. O endereco JDBC precisa ser acessivel pelo ACI, com schema preparado. O Oracle descartavel do CI e o Compose local nao substituem esse banco. O grupo legado com app e Oracle juntos nao sera atualizado pelo novo script.

## Execucao local

Requisitos: Java 21 e Docker com Linux containers. Em PowerShell:

```powershell
.\scripts\new-local-env.ps1
docker compose up -d --build
```

O gerador cria `.env` ignorado pelo Git, com senhas aleatorias. Ele nao sobrescreve um arquivo existente. Credenciais de um volume Oracle ja existente nao sao trocadas ao mudar `.env`; preserve as credenciais ou use um ambiente de testes separado. Nao remova volumes com dados que deseja manter.

API: `http://localhost:8080/`; Swagger: `http://localhost:8080/swagger-ui.html`.

## Testes

Testes unitarios sem banco:

```powershell
.\mvnw.cmd -B -ntp clean verify
```

Mesmo fluxo usado pelo CI, em Bash (Linux ou Git Bash), com Java 21 e Docker disponiveis:

```bash
bash scripts/ci/test-oracle.sh
```

Cria um Oracle isolado, com credenciais aleatorias e porta livre, executa `clean verify -Poracle-it` e remove somente o container criado para essa execucao. O teste de integracao faz cadastro, login JWT, CRUD de pet e SELECTs no Oracle apos inserir, atualizar e excluir. Relatorios ficam em `target/surefire-reports` e `target/failsafe-reports`; o CI os publica na aba Tests.

## Autenticacao e rotas

- Cadastro publico: `POST /api/users`.
- Login: `POST /api/auth/login`.
- Rotas protegidas: `Authorization: Bearer <token>`.
- CRUD: `/api/pets`, `/api/diarioentradas`, `/api/registros`.
- Outros modulos: veterinarios, solicitacoes, vinculos e alertas; payloads atualizados no Swagger.

A rota `/` em `HomeController.java` pode receber uma alteracao visual de codigo para demonstrar o deploy no video. Alterar apenas o README nao atende essa evidencia.

## Demonstracao CRUD

```powershell
.\scripts\demo-crud-sprint4.ps1 -BaseUrl 'http://<DNS-DO-ACI>:8080'
```

O script pausa apos cada operacao para executar e filmar o SELECT no cliente SQL conectado ao banco em nuvem. Nao imprime senha ou token. Interface REST funcionando, isoladamente, nao comprova os SELECTs exigidos.

## Entrega

O PDF final deve conter somente nome completo, RM e turma de cada integrante, link GitHub, link YouTube e link do projeto Azure DevOps. Descricao, stack, diagrama e instrucoes ficam neste README e nos documentos vinculados. Consulte o roteiro completo antes de gravar.

## Equipe

- Arthur dos Santos Cabral - RM566515
- Julia Tiziotto Buttler - RM564975
- Mariana Xavier Quispe - RM566357
- Bruno Martins Bettio - RM564939
- Jose Diogo Da Silva Neves - RM562341

Turmas e nome do professor ainda precisam ser informados para a configuracao final.
