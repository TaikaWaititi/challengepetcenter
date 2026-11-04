# Challenge PetCenter - Sprint 4 DevOps

API REST para acompanhar saude, humor e rotina dos pets. Centraliza o historico para tutores e clinicas e apoia a continuidade do cuidado veterinario.

Stack: Java 21, Spring Boot 4.0.6, Maven Wrapper, Spring MVC, JPA/Hibernate, Bean Validation, Spring Security, JWT, Oracle JDBC e Swagger/OpenAPI. DevOps: GitHub, Azure Pipelines YAML, JUnit/Mockito, Docker, Azure Web App, Oracle Free em VM Azure, VNet, disco gerenciado, Azure CLI e autenticacao federada.

## Estado desta versao

Projeto privado [Sprint 4 – Azure DevOps](https://dev.azure.com/Sprint4fiap/65b112f9-8fca-499e-93fc-86127fc2f2f6), processo Scrum, professor com Basic e Project Administrators. O CI executa oito testes unitarios e um teste de integracao HTTP/SQL com Oracle; o CD publica o JAR testado no Web App. A gravacao pelo grupo e o PDF final com links continuam necessarios.

- [Configuracao, acessos, evidencias e roteiro da entrega](docs/ENTREGA4_DEVOPS.md)
- [Alteracoes e requisitos correspondentes do PDF](docs/ALTERACOES_SPRINT4.md)
- [PetCenter-CI](https://dev.azure.com/Sprint4fiap/65b112f9-8fca-499e-93fc-86127fc2f2f6/_build?definitionId=1)
- [PetCenter-CD](https://dev.azure.com/Sprint4fiap/65b112f9-8fca-499e-93fc-86127fc2f2f6/_build?definitionId=2)

## Ambiente na Azure

API: https://app-petcenter-rm564939-sprint4.azurewebsites.net/ . Swagger: https://app-petcenter-rm564939-sprint4.azurewebsites.net/swagger-ui.html .

Grupo: `rg-petcenter-rm564939-sprint4`. O Oracle fica na VM `vm-petcenter-oracle`, em disco de dados gerenciado e rede privada. O deploy modifica somente o Web App. Os dados do ambiente anterior foram migrados com Data Pump, hash e comparacao das 12 tabelas.

O endereco anterior e mantido por um proxy Nginx no ACI: http://petcenter-rm564939-sprint4.brazilsouth.azurecontainer.io:8080/ . A conexao do proxy ao Web App usa HTTPS com certificado verificado.

## Arquitetura da Sprint 4

![Arquitetura e fluxo CI/CD numerado](docs/arquitetura-sprint4.svg)

O desenvolvedor envia codigo ao GitHub em `master`. O Azure DevOps executa Maven e testes contra Oracle descartavel, publica o JAR como Pipeline Artifact e inicia o CD. O CD verifica o checksum do artefato daquele CI e publica no Azure Web App por identidade federada. A API acessa o Oracle persistente pela VNet; usuarios acessam API/Swagger e o aluno confere o CRUD com SELECT no banco em nuvem.

O pool `PetCenter-Linux` usa um agente em container no Docker Desktop deste computador. Para executar pipelines e gravar a demonstracao, manter computador, Docker Desktop e `petcenter-azuredevops-agent` ligados. A aplicacao e o banco permanecem na Azure independentemente do agente. Veja o guia para reconstruir ou iniciar o agente.

`master` e a branch do gatilho exigido no PDF; `main` acompanha a versao entregue. Alterar somente `main` nao dispara esse CI.

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
.\scripts\demo-crud-sprint4.ps1 -BaseUrl 'https://app-petcenter-rm564939-sprint4.azurewebsites.net'
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

Turma: 2TDSA. Professor: Felipe Cavalcante Vilella de Oliveira (pf2128@fiap.com.br).
