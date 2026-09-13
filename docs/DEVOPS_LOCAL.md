# DevOps Local - Sem Azure

Esta versao adequa o projeto Java Advanced do PetCenter para a etapa local de DevOps, ainda sem criar recursos na Azure.

## O que esta incluido

- API Spring Boot com Java 21.
- Spring Security e JWT preservados da nova versao Java.
- Modulo de alertas da nova versao Java incorporado ao projeto DevOps.
- Dockerfile multi-stage para build da API, com runtime Java 21 reduzido por `jlink`.
- Container Oracle Free como banco do projeto.
- `database/script_bd.sql` com o SQL oficial da etapa de Banco de Dados.
- `database/init/01-script_bd.sql` preparado para inicializar o Oracle automaticamente.
- Roteiro de teste local com CRUD completo, chamadas REST e evidencia por `SELECT` no Oracle.
- Checklist pre-Azure em `docs/CHECKLIST_PRE_AZURE.md`.

## Arquitetura

```text
Swagger / Postman / Insomnia
        |
        v
petcenter-api - Spring Boot + Java 21 + JWT
        |
        v
petcenter-oracle - Oracle Free
        |
        v
Schema PETCENTER criado pelo SQL do projeto
```

## Execucao

```bash
docker compose up -d --build
docker compose ps
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Otimizacao da Imagem Docker

A imagem da API usa Alpine e um runtime Java customizado com `jlink`, contendo apenas os modulos necessarios para o Spring Boot e o Oracle JDBC.

Resultado medido no teste local:

- Antes: aproximadamente 424 MB.
- Depois: aproximadamente 251 MB.

O Maven e o JDK completo ficam apenas nas etapas intermediarias de build e nao entram na imagem final.

## Teste local

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-devops-local.ps1
```

O script cadastra um tutor, autentica com JWT, valida Swagger, verifica o usuario do container da API, exibe o tamanho da imagem Docker, executa CRUD completo em tabelas relacionadas (`pets`, `diario_entradas` e `registros`) e testa o modulo `alertas` da nova versao Java.

## Reset do banco

```bash
docker compose down -v
docker compose up -d --build
```

Use `down -v` somente quando quiser apagar os dados e recriar o schema.
