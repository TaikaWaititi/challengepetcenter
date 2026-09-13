# Checklist Pre-Azure

Este checklist registra o que foi validado antes de iniciar a etapa de Azure.

## Status Atual

- Validado em ambiente local Docker em 12/09/2026 apos adequacao ao novo projeto Java.
- API Java 21 com Spring Boot executando em container Docker.
- Banco Oracle Free executando em container Docker.
- Schema `PETCENTER` criado a partir do SQL do projeto de Banco de Dados.
- Aplicacao conectada ao Oracle por variaveis de ambiente.
- Swagger disponivel em `http://localhost:8080/swagger-ui.html`.
- Container da API executando sem privilegio administrativo: usuario `spring`, `uid=1001`.
- Imagem Docker da API otimizada com runtime Java customizado por `jlink`.
- Correcao aplicada na regra de exclusao de diario para verificar registros vinculados por `entrada_id`.
- Modulo `alertas` da nova versao Java incorporado e validado com Oracle.

## Evidencias Tecnicas Validadas

Comandos usados para evidencia local:

```bash
docker compose ps
docker compose exec -T app id
docker image ls challengepetcenterjava-app
```

Resultado esperado:

- `petcenter-api` em execucao na porta `8080`.
- `petcenter-oracle` em execucao e saudavel na porta `1521`.
- Usuario do app diferente de `root`.
- Imagem `challengepetcenterjava-app` com aproximadamente `251MB`.

## CRUD Completo Validado

O script `scripts/test-devops-local.ps1` valida CRUD completo em tabelas relacionadas:

- `pets`
- `diario_entradas`
- `registros`

Operacoes validadas:

- Inclusao com `POST`.
- Consulta com `GET`.
- Alteracao com `PUT`.
- Exclusao com `DELETE`.

O mesmo roteiro tambem valida:

- Swagger retornando HTTP `200`.
- Cadastro publico de usuario.
- Login com JWT.
- Uso do token JWT nas rotas protegidas.
- Persistencia no Oracle com consultas `SELECT`.
- Criacao, consulta e desativacao de alerta.

Resultado da ultima validacao:

- Swagger retornou HTTP `200`.
- `POST`, `GET`, `PUT` e `DELETE` passaram em `pets`.
- `POST`, `GET`, `PUT` e `DELETE` passaram em `diario_entradas`.
- `POST`, `GET`, `PUT` e `DELETE` passaram em `registros`.
- Exclusoes retornaram HTTP `204`.
- Container da API executou como `uid=1001(spring)`.
- Imagem da API ficou em aproximadamente `251MB`.
- Modulo `alertas` gravou corretamente na tabela `alertas` do Oracle.

Execucao:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-devops-local.ps1
```

## Reset e Teste do Zero

Para comprovar que o ambiente nasce do zero:

```bash
docker compose down -v
docker compose up -d --build
```

Depois rode:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-devops-local.ps1
```

## Pendencias Antes do Azure

Antes de abrir o Azure, resta apenas separar as evidencias visuais para a apresentacao ou entrega:

- Print dos containers no Docker Desktop.
- Print do Swagger aberto.
- Print da execucao do script de teste.
- Print do resultado das consultas no Oracle.
- Print do tamanho da imagem Docker.

## Decisao Recomendada para Azure

Como esta entrega local ja usa API e banco em containers, a opcao mais coerente para seguir no PDF e:

- Azure Container Registry (ACR) para armazenar a imagem da API.
- Azure Container Instance (ACI) para executar os containers.
- Containerizacao completa de App e Banco.

Nao misturar esta opcao com App Service ou banco PaaS.
