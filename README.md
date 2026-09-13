# Challenge PetCenter - Java Advanced + DevOps

API REST em Java com Spring Boot para acompanhamento diario da saude, humor e rotina de pets. A solucao apoia tutores e clinicas veterinarias no registro de eventos relevantes, acompanhamento do historico do animal e integracao com a base relacional do projeto.

Esta versao documenta o estado atual do projeto de DevOps: API Java containerizada, banco Oracle em container, execucao local com Docker Compose e publicacao em Microsoft Azure usando Azure CLI, Azure Container Registry (ACR) e Azure Container Instances (ACI).

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
- Runtime Java customizado com `jlink` para reduzir a imagem final da API

## Arquitetura

```text
Swagger / Postman / Insomnia / Navegador
        |
        v
API Spring Boot - Java 21 + Spring Security + JWT
        |
        v
Oracle Database Free - container petcenter-oracle
        |
        v
Schema PETCENTER criado a partir do SQL do projeto de Banco de Dados
```

Na Azure, a arquitetura utilizada foi:

```text
Usuario
  |
  v
URL publica do Azure Container Instances
  |
  v
Container Group ACI
  |
  +--> petcenter-api
  |       API Java/Spring Boot
  |       Porta publica 8080
  |       Execucao sem usuario root/admin
  |
  +--> petcenter-oracle
          Oracle Database Free
          Porta interna 1521
          Schema PETCENTER
```

## Banco de Dados do Projeto

O projeto de DevOps foi alinhado ao projeto de Banco de Dados. A versao atual utiliza Oracle Database Free em container, e nao H2.

O SQL do projeto de Banco de Dados foi usado para criar o schema `PETCENTER`, incluindo tabelas, relacionamentos, inserts e consultas de evidencia. A inicializacao do banco e feita automaticamente pelo container Oracle quando o ambiente e criado do zero.

Credenciais locais padrao:

```text
Host: localhost
Porta: 1521
Service: FREEPDB1
Usuario: PETCENTER
Senha: petcenter
JDBC: jdbc:oracle:thin:@localhost:1521/FREEPDB1
```

## Docker

O projeto possui uma API Java containerizada e um banco Oracle tambem containerizado.

A imagem da API foi otimizada com build multi-stage:

- Maven usado apenas na etapa de build.
- Runtime Java 21 reduzido com `jlink`.
- Imagem final baseada em Alpine.
- Aplicacao executada por usuario nao-root.

Durante a adequacao de DevOps, a imagem da API foi reduzida de aproximadamente 424 MB para cerca de 251 MB.

## Como Executar Localmente

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

### Veterinarios, Solicitacoes, Vinculos e Alertas

A versao atual tambem foi adequada para ficar alinhada ao projeto Java mais recente, incluindo recursos relacionados a veterinarios, solicitacoes, vinculos entre pets e veterinarios e alertas. A forma mais segura de conferir parametros e payloads atualizados e pelo Swagger.

## Teste Automatizado Local

Com os containers em execucao, rode:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-devops-local.ps1
```

O script valida:

- Swagger local.
- API em execucao.
- Criacao de usuario.
- Login com JWT.
- Uso do token em rotas protegidas.
- Cadastro e consulta de pet.
- Entradas de diario.
- Registros.
- Persistencia no Oracle.
- Usuario nao-root no container da API.
- Tamanho da imagem Docker.

## Azure ACR + ACI

A etapa de Azure foi realizada via Azure CLI usando a opcao ACR + ACI, conforme a abordagem trabalhada para a entrega de DevOps.

Recursos criados:

- Resource Group: `rg-petcenter-rm564939-devops`
- Azure Container Registry: `acrpetcenterrm564939`
- Azure Container Instance: `aci-petcenter-rm564939`
- URL publica: `http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080`
- Swagger publico: `http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080/swagger-ui.html`

Imagens publicadas no ACR:

- `acrpetcenterrm564939.azurecr.io/petcenter-api:latest`
- `acrpetcenterrm564939.azurecr.io/petcenter-oracle:latest`

## Comandos Base da Etapa Azure

Os comandos abaixo representam o fluxo usado na entrega:

```bash
az account show
az provider register --namespace Microsoft.ContainerRegistry --wait
az provider register --namespace Microsoft.ContainerInstance --wait

az group create \
  --name rg-petcenter-rm564939-devops \
  --location brazilsouth

az acr create \
  --resource-group rg-petcenter-rm564939-devops \
  --name acrpetcenterrm564939 \
  --sku Basic \
  --admin-enabled true

az acr login --name acrpetcenterrm564939

docker build -t acrpetcenterrm564939.azurecr.io/petcenter-api:latest .
docker build -t acrpetcenterrm564939.azurecr.io/petcenter-oracle:latest ./database

docker push acrpetcenterrm564939.azurecr.io/petcenter-api:latest
docker push acrpetcenterrm564939.azurecr.io/petcenter-oracle:latest

az container create \
  --resource-group rg-petcenter-rm564939-devops \
  --file ./tmp/azure/container-group.yml
```

O arquivo YAML usado no `az container create` foi renderizado localmente com os valores sensiveis e nao deve ser versionado. A versao correta para documentacao deve conter apenas placeholders.

## Validacao em Nuvem

A publicacao em ACI foi validada com:

- Container `app` em execucao.
- Container `oracle` em execucao.
- Swagger publico retornando HTTP `200`.
- Rota `/` retornando HTTP `200`.
- Cadastro de usuario pela URL publica.
- Login JWT pela URL publica.
- Criacao de pet pela URL publica.

Teste remoto usado:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-azure-public.ps1
```

Resultado esperado:

```json
{
  "status": "OK",
  "swaggerHttp": 200,
  "homeHttp": 200,
  "home": "Challenge PetCenter API funcionando!"
}
```

## Evidencia Manual no Banco

Para acessar o SQLPlus do Oracle local:

```bash
docker compose exec oracle sqlplus PETCENTER/petcenter@FREEPDB1
```

Para executar consultas de evidencia:

```bash
docker compose exec -T oracle sqlplus -s PETCENTER/petcenter@FREEPDB1 "@/database/select-evidencias.sql"
```

## Reset do Banco Local

Para recriar tudo do zero:

```bash
docker compose down -v
docker compose up -d --build
```

O volume do Oracle guarda a persistencia local. Ao usar `down -v`, os dados sao apagados e o script de inicializacao roda novamente.

## Observacao Sobre VM

A entrega final documentada aqui usa ACR + ACI. Scripts antigos baseados em VM Linux podem existir no historico do projeto, mas nao representam a abordagem final adotada nesta versao.

## Custos

A configuracao ACR + ACI gera custo enquanto os recursos estiverem ativos. Para interromper/remover os recursos da entrega:

```bash
az group delete --name rg-petcenter-rm564939-devops --yes --no-wait
```

## Repositorio

```text
https://github.com/TaikaWaititi/challengepetcenter
```

## Equipe

- Arthur dos Santos Cabral - RM566515
- Julia Tiziotto Buttler - RM564975
- Mariana Xavier Quispe - RM566357
- Bruno Martins Bettio - RM564939
- Jose Diogo Da Silva Neves - RM562341
