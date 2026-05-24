# Challenge PetCenter

API REST em Java com Spring Boot para acompanhamento diario da saude, humor e rotina de pets. A solucao apoia a continuidade do cuidado veterinario por meio do cadastro de tutores, pets, entradas de diario e registros estruturados de eventos relevantes.

## Equipe

| Integrante | RM |
| --- | --- |
| Bruno Martins Bettio | RM564939 |
| Jose Diogo Da Silva Neves | RM562341 |
| Arthur dos Santos Cabral | RM566515 |
| Mariana Xavier Quispe | RM566357 |
| Julia Tiziotto Buttler | RM564975 |

## Objetivo do Projeto

O projeto foi desenvolvido para o Challenge proposto pela Clyvo/FIAP. O objetivo e transformar registros cotidianos do pet em informacoes organizadas e consultaveis, permitindo identificar possiveis anomalias comportamentais e apoiar a busca por cuidado veterinario antes que um problema se agrave por meio de um diário que tanto o tutor quanto o veterinário podem ter acesso.

A proposta vai alem de um CRUD simples, permitindo:

- registro continuo da rotina do pet;
- organizacao em linha do tempo;
- persistencia estruturada dos dados;
- apoio ao acompanhamento veterinario;
- futura geracao de insights clinicos.

## Beneficios para o negocio

- Centraliza dados importantes da jornada de saude do pet.
- Ajuda tutores e clinicas a acompanharem historico, rotina e evolucao do animal.
- Facilita a continuidade do cuidado e reduz perda de informacoes entre consultas.
- Cria base de dados para futuras recomendacoes, alertas e analises clinicas.
- Aumenta potencial de recorrencia e fidelizacao para clinicas veterinarias.

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Cache
- Bean Validation
- Maven
- H2 Database
- Hibernate
- Swagger/OpenAPI
- Docker
- Docker Compose
- Azure CLI
- Azure Virtual Machine Linux

## Estrutura do Projeto

```text
Controller -> Service -> Repository -> Banco de Dados
```

```text
src/main/java/com/fiap/challengepetcenter
    controller
    DTO
    exception
    model
    repository
    service
    ChallengepetcenterApplication.java
```

## Arquitetura macro

```text
Usuario / Postman / Insomnia / Swagger
        |
        v
Internet - IP publico da Azure VM
        |
        v
Network Security Group
Portas: 22, 8080, 81
        |
        v
Azure VM Linux Ubuntu
        |
        +--> Docker Engine
              |
              +--> Container petcenter-api
              |       Spring Boot + Java 21
              |       Porta 8080
              |       Usuario nao-root: spring
              |
              +--> Container petcenter-h2db
                      Banco H2
                      Console Web na porta 81
                      Volume nomeado h2-data
```

## Funcionalidades Implementadas

### Usuarios

- Cadastro de usuarios.
- Busca de usuarios.
- Busca por email.
- Atualizacao de usuarios.
- Remocao de usuarios.
- Listagem paginada.

### Pets

- Cadastro de pets.
- Relacionamento entre tutor e pet.
- Busca de pets.
- Busca por tutor.
- Busca por nome.
- Atualizacao de pets.
- Remocao de pets.
- Listagem paginada.

### Diario de Entradas

- Registro diario da rotina do pet.
- Organizacao temporal das informacoes.
- Consulta por data.
- Resumos e observacoes gerais.
- Listagem paginada.

### Registros

- Registro estruturado de alimentacao, comportamento, sintomas e atividades.
- Atualizacao e remocao dos registros.
- Listagem paginada.

## Rotas principais

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

### Diario de entradas

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

## Swagger

Com o projeto em execucao:

```text
http://localhost:8080/swagger-ui.html
```

Na Azure, substituir `localhost` pelo IP publico da VM:

```text
http://IP_PUBLICO:8080/swagger-ui.html
```

## Como executar localmente com Docker

```bash
docker compose up -d --build
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

Console H2:

```text
http://localhost:81
```

No console H2, use:

```text
JDBC URL: jdbc:h2:tcp://localhost:1521/./petcenterdb
User Name: sa
Password:
```

## Como executar na Azure

1. Publique este projeto em um repositorio publico no GitHub.
2. Confirme se o `DevOps.sh` esta apontando para o repositorio correto.
3. Execute no Azure Cloud Shell:

```bash
chmod +x DevOps.sh
./DevOps.sh
```

O script executa em sequencia:

- cria o Resource Group;
- cria uma VM Linux Ubuntu;
- abre as portas `22`, `8080` e `81`;
- instala Docker, Docker Compose, Git, nano e unzip;
- cria o usuario `appuser`;
- clona o repositorio;
- executa API e banco com Docker Compose em background.

## Docker

O projeto possui:

- `Dockerfile`: cria a imagem da API Java.
- `docker-compose.yml`: sobe API e H2 em containers separados.
- `DevOps.sh`: provisiona a infraestrutura Azure com Azure CLI.

### Otimizacao da imagem

O `Dockerfile` usa multi-stage build e `jlink` para gerar um runtime Java customizado. Com isso, a imagem da API fica abaixo de `400MB`.

## Persistencia do banco

O banco H2 roda em container separado e usa o volume nomeado `h2-data`.

Para demonstrar persistencia:

```bash
docker compose down
docker compose up -d
curl http://localhost:8080/api/users
```

Os dados devem continuar disponiveis apos a recriacao dos containers.

## Teste automatizado local

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-devops-local.ps1
```

O script cria usuarios, pet, entrada de diario e registro, validando os principais endpoints.

## Evidencia obrigatoria de exclusao da VM

Ao final da apresentacao, remova os recursos:

```bash
az group delete --name rg-petcenter --yes --no-wait
```

Inclua no PDF final o print da exclusao da VM e dos recursos em nuvem.

## Repositorio

```text
https://github.com/TaikaWaititi/challengepetcenter
```
