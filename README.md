# Challenge PetCenter - Java Advanced + DevOps

API REST em Java com Spring Boot para acompanhamento diario da saude, humor e rotina de pets. A solucao apoia tutores e clinicas veterinarias no registro de eventos relevantes, acompanhamento do historico do animal e integracao com a base relacional do projeto.

Esta versao combina a entrega de Java Advanced com a preparacao de DevOps local e a publicacao em Microsoft Azure usando ACR + ACI.

Atualizacao: o projeto foi adequado para refletir a versao Java mais recente, incorporando tambem o modulo de alertas e a configuracao OpenAPI.

## Objetivo do Projeto

O projeto foi desenvolvido para o Challenge proposto pela Clyvo/FIAP. O objetivo e transformar registros cotidianos do pet em informacoes organizadas e consultaveis, permitindo identificar possiveis anomalias comportamentais e apoiar a busca por cuidado veterinario antes que um problema se agrave.

## Beneficios para o Negocio

- Centraliza dados importantes da jornada de saude do pet.
- Ajuda tutores e clinicas a acompanharem historico, rotina e evolucao do animal.
- Facilita a continuidade do cuidado e reduz perda de informacoes entre consultas.
- Cria base de dados para futuras recomendacoes, alertas e analises clinicas.
- Aumenta potencial de recorrencia e fidelizacao para clinicas veterinarias.

## Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- Bean Validation
- Maven
- Oracle Database Free em container
- Oracle JDBC Driver
- Swagger/OpenAPI
- Docker
- Docker Compose
- Azure CLI
- Azure Container Registry
- Azure Container Instances
- Runtime Java customizado com jlink para reduzir a imagem final da API

## Arquitetura

```text
Swagger / Postman / Insomnia
        |
        v
API Spring Boot - Java 21 + Spring Security + JWT
        |
        v
Oracle Database Free - container petcenter-oracle
        |
        v
Schema PETCENTER criado com database/script_bd.sql
```

## Banco de Dados do Projeto

O arquivo `database/script_bd.sql` contem o SQL da etapa de Banco de Dados do projeto PetCenter. Ele inclui tabelas, relacionamentos, procedures, funcoes, trigger, inserts e blocos de consulta.

Para a inicializacao automatica do container Oracle, ha uma copia preparada em `database/init/01-script_bd.sql`. Essa copia preserva o conteudo do SQL e adiciona os separadores necessarios para executar blocos PL/SQL durante o startup do container.

A imagem Docker da API usa build multi-stage: o Maven fica apenas na etapa de build e a imagem final roda em Alpine com um runtime Java 21 minimo criado com `jlink`. No teste local, a imagem da API foi reduzida de aproximadamente 424 MB para 251 MB.

As migrations H2/Flyway da entrega Java original nao sao usadas nesta versao DevOps, pois o requisito desta etapa remove o H2 e utiliza o Oracle em container com o SQL oficial do projeto de Banco de Dados.

Credenciais locais padrao:

```text
Host: localhost
Porta: 1521
Service: FREEPDB1
Usuario: PETCENTER
Senha: petcenter
JDBC: jdbc:oracle:thin:@localhost:1521/FREEPDB1
```

## Como Executar

Suba a API e o banco:

```bash
docker compose up -d --build
```

Verifique os containers:

```bash
docker compose ps
```

API:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Logs da API:

```bash
docker compose logs -f app
```

Logs do Oracle:

```bash
docker compose logs -f oracle
```

## Autenticacao

O cadastro de usuario e publico:

```http
POST /api/users
```

O login gera o token JWT:

```http
POST /api/auth/login
```

Os demais endpoints protegidos devem receber:

```text
Authorization: Bearer <token>
```

## Rotas Principais

### Auth

- `POST /api/auth/login`

### Users

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `GET /api/users/email/{email}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

### Pets

- `POST /api/pets`
- `GET /api/pets`
- `GET /api/pets/{id}`
- `GET /api/pets/user/{userId}`
- `GET /api/pets/nome/{nome}`
- `PUT /api/pets/{id}`
- `DELETE /api/pets/{id}`

### Diario de Entradas

- `POST /api/diarioentradas`
- `GET /api/diarioentradas`
- `GET /api/diarioentradas/{id}`
- `GET /api/diarioentradas/data?data=2026-05-11`
- `PUT /api/diarioentradas/{id}`
- `DELETE /api/diarioentradas/{id}`

### Registros

- `POST /api/registros`
- `GET /api/registros`
- `GET /api/registros/{id}`
- `PUT /api/registros/{id}`
- `DELETE /api/registros/{id}`

### Veterinarios, Solicitacoes e Vinculos

A nova versao Java tambem contem endpoints para veterinarios, solicitacoes, vinculos entre pets e veterinarios e alertas. Consulte o Swagger para ver os parametros atualizados.

### Alertas

- `POST /api/alertas`
- `GET /api/alertas`
- `GET /api/alertas/{id}`
- `GET /api/alertas/pet/{petId}`
- `PATCH /api/alertas/{id}/desativar`

## Teste Automatizado Local

Com os containers em execucao, rode:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-devops-local.ps1
```

O script cadastra um tutor, faz login, usa o JWT nas rotas protegidas, valida Swagger, verifica o usuario do container da API, exibe o tamanho da imagem Docker, executa CRUD completo em tabelas relacionadas (`pets`, `diario_entradas` e `registros`) e valida o modulo de alertas da nova versao Java. Ao final, executa `SELECT` direto no Oracle para comprovar a persistencia.

## Checklist Pre-Azure

O arquivo `docs/CHECKLIST_PRE_AZURE.md` resume o que ja foi validado, incluindo o teste do ambiente recriado do zero, e quais prints ainda podem ser separados para a entrega antes de iniciar Azure.

## Azure ACR + ACI

A etapa de Azure foi realizada via Azure CLI usando a opcao ACR + ACI.

Recursos criados durante a validacao:

- Resource Group: `rg-petcenter-rm564939-devops`
- Azure Container Registry: `acrpetcenterrm564939`
- Azure Container Instance: `aci-petcenter-rm564939`
- URL publica usada na validacao: `http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080`
- Swagger publico usado na validacao: `http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080/swagger-ui.html`

Imagens publicadas no ACR:

- `acrpetcenterrm564939.azurecr.io/petcenter-api:latest`
- `acrpetcenterrm564939.azurecr.io/petcenter-oracle:latest`

O arquivo `azure/container-group.template.yml` documenta a estrutura usada para criar o grupo de containers no ACI. O arquivo renderizado com valores sensiveis foi usado apenas temporariamente e nao deve ser versionado.

Os comandos e evidencias da etapa Azure estao documentados em `docs/AZURE_ACR_ACI.md`.

## Evidencia Manual no Banco

Para acessar o SQLPlus do Oracle:

```bash
docker compose exec oracle sqlplus PETCENTER/petcenter@FREEPDB1
```

Para executar as consultas de evidencia:

```bash
docker compose exec -T oracle sqlplus -s PETCENTER/petcenter@FREEPDB1 "@/database/select-evidencias.sql"
```

## Reset do Banco

Para recriar tudo do zero:

```bash
docker compose down -v
docker compose up -d --build
```

O volume `oracle-data` guarda a persistencia. Ao usar `down -v`, os dados sao apagados e o script de inicializacao roda novamente.

## Observacao Sobre Azure DevOps

Esta entrega cobre ACR + ACI via Azure CLI. Pipelines de CI/CD no Azure DevOps ainda nao foram adicionados, pois esta etapa foi focada na criacao dos recursos e deploy dos containers conforme a opcao ACR + ACI.

## Equipe

- Arthur dos Santos Cabral - RM566515
- Julia Tiziotto Buttler - RM564975
- Mariana Xavier Quispe - RM566357
- Bruno Martins Bettio - RM564939
- Jose Diogo Da Silva Neves - RM562341
