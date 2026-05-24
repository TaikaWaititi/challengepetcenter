# Challenge PetCenter

API REST em Java com Spring Boot para apoiar a continuidade do cuidado de pets. A solucao permite cadastrar tutores, pets, entradas de diario de acompanhamento e registros de eventos como alimentacao, humor, observacoes e status de saude.

## Equipe

| Integrante | RM |
| --- | --- |
| Bruno Martins Bettio | RM564939 |
| Jose Diogo Da Silva Neves | RM562341 |
| Arthur dos Santos Cabral | RM566515 |
| Mariana Xavier Quispe | RM566357 |
| Julia Tiziotto Buttler | RM564975 |

## Beneficios para o negocio

- Centraliza dados importantes da jornada de saude do pet.
- Ajuda tutores e clinicas a acompanharem historico, rotina e evolucao do animal.
- Facilita a continuidade do cuidado, reduzindo perda de informacoes entre consultas.
- Cria base de dados para futuras recomendacoes, alertas e analises clinicas.

## Arquitetura macro

```text
Usuario / Postman / Insomnia
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
        +--> Container petcenter-api
        |       Spring Boot + Java 21
        |       Porta 8080
        |
        +--> Container petcenter-h2db
                Banco H2
                Console Web na porta 81
                Volume nomeado h2-data
```

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- H2 Database
- Swagger/OpenAPI
- Docker
- Docker Compose
- Azure CLI
- Azure Virtual Machine Linux

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

## Como executar na Azure

1. Publique este projeto em um repositorio publico no GitHub.
2. Abra o arquivo `DevOps.sh`.
3. Altere a variavel `GITHUB_REPO_URL` para o link do repositorio do grupo.
4. Execute no Azure Cloud Shell:

```bash
chmod +x DevOps.sh
./DevOps.sh
```

O script executa em sequencia:

- Cria o Resource Group.
- Cria uma VM Linux Ubuntu.
- Abre as portas `22`, `8080` e `81`.
- Instala Docker, Docker Compose, Git, nano e unzip.
- Cria o usuario `appuser`.
- Clona o repositorio.
- Executa a aplicacao e o banco com Docker Compose em background.

Ao final, a API ficara disponivel em:

```text
http://IP_PUBLICO:8080
```

Swagger:

```text
http://IP_PUBLICO:8080/swagger-ui.html
```

Console H2:

```text
http://IP_PUBLICO:81
```

No console H2, use:

```text
JDBC URL: jdbc:h2:tcp://localhost:1521/./petcenterdb
User Name: sa
Password:
```

## Docker

Executar manualmente:

```bash
docker compose up -d --build
```

Ver containers:

```bash
docker compose ps
```

Ver logs:

```bash
docker compose logs -f app
```

Parar:

```bash
docker compose down
```

### Otimizacao da imagem

O `Dockerfile` usa multi-stage build e `jlink` para gerar um runtime Java customizado. Com isso, a imagem da API foi reduzida de aproximadamente `575MB` para `234MB`, ficando abaixo da meta de `400MB`.

## Persistencia do banco

O banco H2 usa o volume nomeado `h2-data`.

Para demonstrar persistencia:

1. Cadastre pelo menos dois registros pela API.
2. Execute `docker compose down`.
3. Execute `docker compose up -d`.
4. Consulte os dados novamente pela API ou pelo H2 Console.

## Evidencias de teste local

Testes realizados em Docker local antes do deploy em nuvem:

- `docker compose up -d --build`: API e H2 subiram em background.
- `docker compose ps`: containers `petcenter-api` e `petcenter-h2db` ficaram com status `Up`.
- `docker inspect petcenter-api --format "Container user: {{.Config.User}}"`: retornou `spring`, comprovando execucao sem usuario root.
- `docker volume ls --filter name=challengepetcenter_h2-data`: comprovou o volume nomeado `challengepetcenter_h2-data`.
- CRUD validado com `POST`, `GET`, `PUT` e `DELETE` em `/api/users`.
- Inserts significativos validados tambem em `/api/pets`, `/api/diarioentradas` e `/api/registros`.
- Persistencia validada apos `docker compose down` e `docker compose up -d`, com os registros ainda disponiveis pela API.

## Evidencia obrigatoria de exclusao da VM

Ao final da apresentacao, remova os recursos para evitar gasto de creditos:

```bash
az group delete --name rg-petcenter --yes --no-wait
```

Tire print da exclusao no portal Azure ou do comando executado, pois o PDF exige evidencia da remocao da VM e dos recursos em nuvem.
