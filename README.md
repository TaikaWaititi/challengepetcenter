# Challenge PetCenter - Java Advanced + DevOps

API REST em Java com Spring Boot para acompanhamento diário da saúde, humor e rotina de pets. A solução apoia tutores e clínicas veterinárias no registro de eventos relevantes, acompanhamento do histórico do animal e integração com a base relacional do projeto.

Esta versão documenta o estado atual do projeto de DevOps: API Java containerizada, banco Oracle em container, execução local com Docker Compose e publicação em Microsoft Azure usando Azure CLI, Azure Container Registry (ACR) e Azure Container Instances (ACI).

## Objetivo do Projeto

O projeto foi desenvolvido para o Challenge proposto pela Clyvo. O objetivo é transformar registros cotidianos do pet em informações organizadas e consultáveis, permitindo identificar possíveis anomalias comportamentais e apoiar a busca por cuidado veterinário antes que um problema se agrave.

## Benefícios para o Negócio

- Centraliza dados importantes da jornada de saúde do pet.
- Ajuda tutores e clínicas a acompanharem histórico, rotina e evolução do animal.
- Facilita a continuidade do cuidado e reduz perda de informações entre consultas.
- Cria base de dados para futuras recomendações, alertas e análises clínicas.
- Aumenta potencial de recorrência e fidelização para clínicas veterinárias.

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
Usuário
  |
  v
URL pública do Azure Container Instances
  |
  v
Container Group ACI
  |
  +--> petcenter-api
  |       API Java/Spring Boot
  |       Porta pública 8080
  |       Execução sem usuário root/admin
  |
  +--> petcenter-oracle
          Oracle Database Free
          Porta interna 1521
          Schema PETCENTER
```

## Banco de Dados do Projeto

O projeto de DevOps foi alinhado ao projeto de Banco de Dados. A versão atual foi alterada para não usar H2.

O SQL do projeto de Banco de Dados foi usado para criar o schema `PETCENTER`, incluindo tabelas, relacionamentos, inserts e consultas de evidência. A inicialização do banco é feita automaticamente pelo container Oracle quando o ambiente é criado do zero.

Credenciais locais padrão:

```text
Host: localhost
Porta: 1521
Service: FREEPDB1
Usuário: PETCENTER
Senha: petcenter
JDBC: jdbc:oracle:thin:@localhost:1521/FREEPDB1
```

## Docker

O projeto possui uma API Java containerizada e um banco Oracle também containerizado.

A imagem da API foi otimizada com build multi-stage:

- Maven usado apenas na etapa de build.
- Runtime Java 21 reduzido com `jlink`.
- Imagem final baseada em Alpine.
- Aplicação executada por usuário não-root.

Durante a adequação de DevOps, a imagem da API foi reduzida de aproximadamente 424 MB para cerca de 251 MB.

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

## Autenticação

O cadastro de usuário é público:

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

### Diário de Entradas

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

### Veterinários, Solicitações, Vínculos e Alertas

A versão atual também foi adequada para ficar alinhada ao projeto Java mais recente, incluindo recursos relacionados a veterinários, solicitações, vínculos entre pets e veterinários e alertas. A forma mais segura de conferir parâmetros e payloads atualizados é pelo Swagger.

## Teste Automatizado Local

Com os containers em execução, rode:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-devops-local.ps1
```

O script valida:

- Swagger local.
- API em execução.
- Criação de usuário.
- Login com JWT.
- Uso do token em rotas protegidas.
- Cadastro e consulta de pet.
- Entradas de diário.
- Registros.
- Persistência no Oracle.
- Usuário não-root no container da API.
- Tamanho da imagem Docker.

## Azure ACR + ACI

A etapa de Azure foi realizada via Azure CLI usando a opção ACR + ACI, conforme a entrega de DevOps requisitou.

Recursos criados:

- Resource Group: `rg-petcenter-rm564939-devops`
- Azure Container Registry: `acrpetcenterrm564939`
- Azure Container Instance: `aci-petcenter-rm564939`
- URL pública: `http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080`
- Swagger público: `http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080/swagger-ui.html`

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

O arquivo YAML usado no `az container create` foi renderizado localmente com os valores sensíveis e não deve ser versionado. A versão correta para documentação deve conter apenas placeholders.

## Validação em Nuvem

A publicação em ACI foi validada com:

- Container `app` em execução.
- Container `oracle` em execução.
- Swagger público retornando HTTP `200`.
- Rota `/` retornando HTTP `200`.
- Cadastro de usuário pela URL pública.
- Login JWT pela URL pública.
- Criação de pet pela URL pública.

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

## Evidência Manual no Banco

Para acessar o SQLPlus do Oracle local:

```bash
docker compose exec oracle sqlplus PETCENTER/petcenter@FREEPDB1
```

Para executar consultas de evidência:

```bash
docker compose exec -T oracle sqlplus -s PETCENTER/petcenter@FREEPDB1 "@/database/select-evidencias.sql"
```

## Reset do Banco Local

Para recriar tudo do zero:

```bash
docker compose down -v
docker compose up -d --build
```

O volume do Oracle guarda a persistência local. Ao usar `down -v`, os dados são apagados e o script de inicialização roda novamente.

## Observação Sobre VM

A entrega final documentada aqui usa ACR + ACI. Scripts antigos baseados em VM Linux podem existir no histórico do projeto, mas não representam a abordagem final adotada nesta versão.

## Custos

A configuração ACR + ACI gera custo enquanto os recursos estiverem ativos. Para interromper/remover os recursos da entrega:

```bash
az group delete --name rg-petcenter-rm564939-devops --yes --no-wait
```

## Repositório

```text
https://github.com/TaikaWaititi/challengepetcenter
```

## Equipe

- Arthur dos Santos Cabral - RM566515
- Julia Tiziotto Buttler - RM564975
- Mariana Xavier Quispe - RM566357
- Bruno Martins Bettio - RM564939
- José Diogo Da Silva Neves - RM562341
