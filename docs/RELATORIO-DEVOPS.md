# Relatorio DevOps - Challenge PetCenter

## Folha de rosto

Projeto: Challenge PetCenter

Disciplina: DevOps Tools & Cloud Computing

Equipe:

| Integrante | RM |
| --- | --- |
| Bruno Martins Bettio | RM564939 |
| Jose Diogo Da Silva Neves | RM562341 |
| Arthur dos Santos Cabral | RM566515 |
| Mariana Xavier Quispe | RM566357 |
| Julia Tiziotto Buttler | RM564975 |

## Indice

1. Descricao da solucao
2. Arquitetura macro
3. Containerizacao
4. Script Azure CLI
5. Testes de CRUD
6. Persistencia do banco
7. Links da entrega
8. Evidencia de remocao da VM

## 1. Descricao da solucao

O Challenge PetCenter e uma API REST em Java com Spring Boot voltada para continuidade do cuidado de pets. A aplicacao permite cadastrar tutores, pets, entradas de diario e registros de acompanhamento, apoiando uma jornada mais continua, preventiva e organizada. Esta versao considera a base atualizada do projeto JavaFinal, com validacao, Swagger documentado, paginacao e recursos de cache.

## 2. Arquitetura macro

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
        |       Usuario nao-root: spring
        |
        +--> Container petcenter-h2db
                Banco H2
                Console Web na porta 81
                Volume nomeado h2-data
```

## 3. Containerizacao

A solucao usa:

- `Dockerfile` para construir a imagem da API Java.
- `docker-compose.yml` para executar API e banco H2.
- Container `petcenter-api` para a aplicacao.
- Container `petcenter-h2db` para o banco.
- Volume nomeado `h2-data` para persistencia.
- Multi-stage build e `jlink` para manter a imagem da API em aproximadamente `234MB`, abaixo de `400MB`.

## 4. Script Azure CLI

O arquivo `DevOps.sh` executa:

- criacao do Resource Group;
- criacao da VM Linux Ubuntu;
- abertura das portas `22`, `8080` e `81`;
- instalacao de Docker, Docker Compose, Git, nano e unzip;
- criacao do usuario `appuser`;
- clone do repositorio;
- execucao dos containers em background.

## 5. Testes de CRUD

Testes locais realizados com sucesso:

- `POST /api/users`: criacao de dois usuarios.
- `GET /api/users`: listagem dos usuarios.
- `PUT /api/users/{id}`: atualizacao de usuario.
- `DELETE /api/users/{id}`: remocao de usuario.
- `POST /api/pets`: cadastro de pet.
- `POST /api/diarioentradas`: cadastro de entrada de diario.
- `POST /api/registros`: cadastro de registro de cuidado.
- Listagens paginadas em `GET /api/users`, `GET /api/pets`, `GET /api/diarioentradas` e `GET /api/registros`.

Dados usados no teste:

- Usuario: Bruno DevOps Atualizado.
- Pet: Bolt DevOps.
- Diario: Pet apresentou boa evolucao no teste de continuidade do cuidado.
- Registro: Alimentacao, racao, 250 gramas.

## 6. Persistencia do banco

A persistencia foi validada localmente com:

```bash
docker compose down
docker compose up -d
```

Apos reiniciar os containers, os dados continuaram disponiveis pela API. Isso comprova o uso do volume nomeado `h2-data`.

## 7. Links da entrega

Repositorio GitHub:

```text
https://github.com/TaikaWaititi/challengepetcenter
```

Video no YouTube:

```text
INSERIR_LINK_DO_VIDEO
```

## 8. Evidencia de remocao da VM

Ao final da gravacao, executar:

```bash
az group delete --name rg-petcenter --yes --no-wait
```

Inserir nesta secao o print da exclusao da VM e dos recursos em nuvem.
