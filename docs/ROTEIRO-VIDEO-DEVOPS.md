# Roteiro do Video - DevOps

## 1. Abertura

Apresentar o projeto Challenge PetCenter e dizer que a demonstracao cobre a entrega de DevOps: infraestrutura Azure, Docker, API Java, banco H2 containerizado, CRUD e persistencia.

## 2. Mostrar repositorio

Mostrar no GitHub:

- `README.md`
- `Dockerfile`
- `docker-compose.yml`
- `DevOps.sh`
- pasta `src` da API Java

## 3. Executar script Azure CLI

No Azure Cloud Shell:

```bash
chmod +x DevOps.sh
./DevOps.sh
```

Explicar que o script:

- cria Resource Group;
- cria VM Linux Ubuntu;
- abre portas `22`, `8080` e `81`;
- instala Docker, Docker Compose, Git, nano e unzip;
- cria usuario `appuser`;
- clona o projeto;
- sobe API e banco em background.

## 4. Mostrar containers na VM

Na VM:

```bash
cd /opt/challengepetcenter
docker compose ps
docker inspect petcenter-api --format "Container user: {{.Config.User}}"
docker volume ls
```

Explicar:

- `petcenter-api` e `petcenter-h2db` estao `Up`;
- API nao roda como root;
- volume nomeado persiste os dados do banco.

## 5. Testar pelo IP publico

Abrir:

```text
http://IP_PUBLICO:8080/swagger-ui.html
http://IP_PUBLICO:81
```

Importante: nao usar localhost no video final. O PDF exige teste externo.

## 6. Demonstrar CRUD

Usar Swagger, Postman, Insomnia ou terminal.

Criar dois usuarios:

```bash
curl -X POST http://IP_PUBLICO:8080/api/users -H "Content-Type: application/json" -d '{"nome":"Bruno DevOps","email":"bruno.devops@petcenter.com","senha":"123456","telefone":"11 90000-0001","tipoUsuario":"Tutor"}'
curl -X POST http://IP_PUBLICO:8080/api/users -H "Content-Type: application/json" -d '{"nome":"Mariana DevOps","email":"mariana.devops@petcenter.com","senha":"123456","telefone":"11 90000-0002","tipoUsuario":"Tutor"}'
```

Listar:

```bash
curl http://IP_PUBLICO:8080/api/users
```

Atualizar:

```bash
curl -X PUT http://IP_PUBLICO:8080/api/users/1 -H "Content-Type: application/json" -d '{"nome":"Bruno DevOps Atualizado","email":"bruno.devops@petcenter.com","senha":"123456","telefone":"11 91111-1111","tipoUsuario":"Tutor"}'
```

Excluir:

```bash
curl -X DELETE http://IP_PUBLICO:8080/api/users/2
```

## 7. Demonstrar dados de negocio

Criar tambem:

- um pet;
- uma entrada de diario;
- um registro de cuidado.

Isso deixa claro que os inserts tem conteudo significativo.

## 8. Demonstrar persistencia

Na VM:

```bash
docker compose down
docker compose up -d
curl http://IP_PUBLICO:8080/api/users
curl http://IP_PUBLICO:8080/api/pets
curl http://IP_PUBLICO:8080/api/diarioentradas
curl http://IP_PUBLICO:8080/api/registros
```

Explicar que os dados continuaram porque o banco usa o volume nomeado `h2-data`.

## 9. Excluir recursos Azure

No final:

```bash
az group delete --name rg-petcenter --yes --no-wait
```

Tirar print do comando ou do portal mostrando a exclusao dos recursos.

