# DevOps - Challenge PetCenter

Veja tambem o `README.md`, que concentra os itens cobrados no PDF: descricao do projeto, beneficios de negocio, arquitetura macro, rotas, how-to, Docker e Azure CLI.

## O que a entrega de DevOps pede

- Script Azure CLI criando uma VM Linux.
- Abertura das portas usadas pelo projeto.
- Instalacao do Docker, Docker Compose, Git e ferramentas como nano.
- Aplicacao Java executando em container.
- Banco H2 executando em container separado.
- Execucao em background com `docker compose up -d`.
- Aplicacao rodando sem usuario root.
- Volume nomeado para persistir os dados do banco.
- Testes externos usando o IP publico da VM.
- Evidencia final de exclusao da VM e dos recursos em nuvem.

## Arquivos importantes

- `Dockerfile`: monta a imagem da API Spring Boot com Java 21.
- `docker-compose.yml`: sobe dois containers, um para a API e outro para o H2.
- `DevOps.sh`: script Azure CLI para criar a infraestrutura e publicar a aplicacao.

## Como executar

1. Suba este projeto para um repositorio publico no GitHub.
2. No arquivo `DevOps.sh`, altere a variavel `GITHUB_REPO_URL`.
3. Execute no Azure Cloud Shell:

```bash
chmod +x DevOps.sh
./DevOps.sh
```

4. Ao final, acesse:

```text
http://IP_PUBLICO:8080
http://IP_PUBLICO:8080/swagger-ui.html
http://IP_PUBLICO:81
```

No console do H2, use:

```text
JDBC URL: jdbc:h2:tcp://localhost:1521/./petcenterdb
User Name: sa
Password:
```

5. Teste pelo Postman ou Insomnia os endpoints de `users`, `pets`, `diarioentradas` e `registros`.

6. Depois da gravacao e dos prints, apague os recursos:

```bash
az group delete --name rg-petcenter --yes --no-wait
```

## Persistencia

O banco H2 usa o volume nomeado `h2-data`. Isso evita perda dos dados quando o container for recriado.

Para demonstrar persistencia no video:

```bash
docker compose ps
docker compose down
docker compose up -d
```

Depois consulte novamente os registros pela API.
