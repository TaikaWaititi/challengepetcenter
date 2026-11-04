> Registro historico. O estado atual, acessos e pipelines estao em [Entrega 4 DevOps](ENTREGA4_DEVOPS.md). As pendencias abaixo retratam a data de cada etapa.

> Atualizacao: os nomes e o endereco atuais estao no [relatorio de migracao](MIGRACAO_NOMES.md). O relato abaixo preserva o historico do primeiro teste e seus nomes anteriores.

# Teste funcional em nuvem - PetCenter - 04/11/2026

**Resultado: APROVADO para os fluxos testados.** A API e o Oracle foram publicados manualmente na Azure; cadastro, login JWT e CRUD de pets e diarios foram executados pela URL publica. SELECTs dentro do Oracle da Azure confirmaram os dados apos as operacoes. Esta execucao nao usou Azure DevOps e nao comprova ainda o gatilho CI/CD.

- [API publicada](http://petcenter-s4-564939-20261104.brazilsouth.azurecontainer.io:8080/)
- [Swagger](http://petcenter-s4-564939-20261104.brazilsouth.azurecontainer.io:8080/swagger-ui.html)
- [OpenAPI](http://petcenter-s4-564939-20261104.brazilsouth.azurecontainer.io:8080/api-docs)

## Passo a passo efetivamente executado

### 1. Conferir a conta e os recursos existentes

Consultei `az account show`, `az account list` e `az resource list`. A assinatura ativa estava habilitada. Nao havia ACR ou ACI anterior listado; somente o Network Watcher. Nao alterei o projeto PetCenter no Azure DevOps.

Consultei as politicas da assinatura e confirmei `brazilsouth` entre as regioes permitidas. Os providers Microsoft.ContainerRegistry e Microsoft.ContainerInstance ja estavam registrados.

### 2. Conferir o artefato local que seria publicado

Comparei o SHA-256 do JAR em `target/challengepetcenter-0.0.1-SNAPSHOT.jar` com `/app/app.jar` dentro da imagem runtime. Os dois corresponderam a:

`b9861ffd7cd7f793e1cead81625cd186070889d740a0b52da3d36e3d7796a0f1`

Usei a imagem runtime ja construida para o CD e reconstruí a imagem Oracle com o SQL canonico e a inicializacao sem senha fixa. A API nao foi recompilada durante a publicacao. Os testes locais anteriores haviam aprovado oito testes unitarios e um teste de integracao.

### 3. Criar recursos isolados de teste

```powershell
az group create --name rg-petcenter-s4-test-20261104 --location brazilsouth --tags purpose=sprint4-functional-test project=PetCenter --output none
az acr create --resource-group rg-petcenter-s4-test-20261104 --name acrpetcenters4b5649391104 --location brazilsouth --sku Basic --admin-enabled true --output none
az acr login --name acrpetcenters4b5649391104
```

Gerei senhas aleatorias para Oracle e schema e uma chave JWT aleatoria. Os valores ficaram somente em arquivos de trabalho privados locais e nos campos `secureValue` do ACI, sem serem adicionados ao repositorio ou aos arquivos de resultados.

Para este ensaio manual isolado, o ACR usa credencial de administrador do registro privado. A configuracao de CD proposta no repositorio continua utilizando identidade gerenciada para pull e service connection para push; nao foi substituida por essa credencial.

### 4. Enviar as imagens para o ACR

Apliquei a tag `cloudtest-20261104` e executei `docker push` para:

- `acrpetcenters4b5649391104.azurecr.io/petcenter-api:cloudtest-20261104`
- `acrpetcenters4b5649391104.azurecr.io/petcenter-oracle:cloudtest-20261104`

Digests publicados:

- API: `sha256:27a4c1752d7c9aa1ac29aeffe7ac2f6b77f192b831554678535a15abb32c03ac`
- Oracle: `sha256:9da35b4316d2f2b0318842495289e842256019ca16c217a77ec7674361290238`

### 5. Implantar os containers no ACI

Gerei um arquivo de definicao de grupo com os valores sensiveis fora do repositorio e executei:

```powershell
az container create --resource-group rg-petcenter-s4-test-20261104 --file <arquivo-local-com-segredos> --only-show-errors --output none
```

Configuracao usada:

- Grupo ACI: `aci-petcenter-s4-test`.
- Regiao: `brazilsouth`.
- Oracle: 1 vCPU e 3 GiB de memoria.
- API: 1 vCPU e 1,5 GiB de memoria.
- Unica porta publica: TCP 8080. A porta Oracle 1521 nao foi publicada.
- API conectada ao Oracle pelo endereco interno `localhost:1521/FREEPDB1` do grupo.
- Hibernate DDL automatico desabilitado; schema inicializado pelo SQL do projeto.
- Tempo maior de inicializacao do pool JDBC para aguardar o primeiro startup do Oracle.

Esse grupo conjunto foi criado **exclusivamente como ambiente descartavel para o teste funcional solicitado**. Ele nao e o destino do script de CD definitivo, que exige API separada do banco para preservar o ciclo de vida dos dados. Nenhum banco ou grupo antigo foi substituido.

### 6. Aguardar disponibilidade e verificar a execucao

Aguardei o primeiro startup do Oracle, a execucao do SQL de inicializacao e o retorno HTTP 200 da API. As primeiras conexoes foram recusadas enquanto o banco ainda inicializava; apos a prontidao, o atendimento HTTP funcionou normalmente.

Confirmei com Azure CLI:

- Provisionamento `Succeeded`.
- Containers `oracle` e `app` em `Running`, ambos com zero reinicios no encerramento da verificacao.
- `id` dentro da API retornou `uid=1001(spring)`, sem root.
- Pagina inicial, Swagger e OpenAPI retornaram HTTP 200.

### 7. Executar o CRUD pela URL publica e conferir SELECTs na Azure

Criei um tutor exclusivo de teste e fiz login. Cadastrei e consultei um pet, atualizei seu nome, criei/consultei/atualizei um diario associado, exclui o diario e depois o pet. Ao final, removi o tutor.

Os SELECTs foram executados com SQLPlus **dentro do container Oracle na Azure**, por `az container exec`. Para isso foi usada autenticacao local de sistema no container, sem publicar a porta do banco nem expor senha. Ajustei a chamada do CLI no Windows para preservar corretamente os argumentos; essa correcao foi na ferramenta de verificacao, nao no codigo Java.

Evidencias confirmadas na execucao aprovada:

| Operacao | Consulta real | Resultado |
|---|---|---|
| Pet inserido | `SELECT nome FROM pets WHERE id=6` | `Luna Nuvem` |
| Pet consultado | `SELECT COUNT(*) FROM pets WHERE id=6` | `1` |
| Pet atualizado | `SELECT nome FROM pets WHERE id=6` | `Luna Nuvem Atualizada` |
| Diario inserido | `SELECT resumo FROM diario_entradas WHERE id=4` | `Diario criado na Azure` |
| Diario atualizado | `SELECT resumo FROM diario_entradas WHERE id=4` | `Diario atualizado na Azure` |
| Diario excluido | `SELECT COUNT(*) FROM diario_entradas WHERE id=4` | `0` |
| Pet excluido | `SELECT COUNT(*) FROM pets WHERE id=6` | `0` |

As operacoes POST retornaram 201, GET/PUT/login retornaram 200 e DELETE retornou 204. As respostas de consulta e atualizacao tambem foram conferidas pelo script, alem dos SELECTs.

### 8. Limpar os registros e registrar os resultados

Removi os dados exclusivos da execucao aprovada e os registros temporarios das duas tentativas anteriores de consulta SQL. Confirmei zero usuarios com o prefixo gerado para esses testes. Os dados de exemplo do schema foram preservados.

Os resultados sem senhas e tokens foram salvos em no arquivo de resultados entregue ao usuario. Nenhum commit, push, criacao de pipeline, alteracao de permissao ou modificacao no projeto Azure DevOps foi realizada.

## O que este teste comprova em relacao ao PDF

- p.48: aplicacao Java e banco em nuvem funcionando no ambiente testado.
- p.49, item 3: Oracle em container na nuvem.
- p.52: API acessivel e CRUD de duas entidades relacionadas, com verificacao direta por SELECT.

O teste nao comprova os gatilhos de `master`, build/testes/artefato no Azure DevOps, CD automatico, acesso Basic do professor nem o video de entrega. A publicacao foi manual. Os testes cobriram os fluxos descritos; nao representam uma auditoria funcional de todos os modulos Java.

## Estado deixado para acesso do grupo

O ambiente foi deixado **ativo** para voces acessarem o Swagger e realizarem seus testes. Ele utiliza Oracle descartavel, sem volume persistente declarado; nao usar esse grupo como armazenamento definitivo nem como alvo do novo CD.

ACI e ACR geram cobranca enquanto existirem/estiverem ativos conforme o servico. Para pausar a computacao do teste:

```powershell
az container stop --resource-group rg-petcenter-s4-test-20261104 --name aci-petcenter-s4-test
```

Parar/desalocar o grupo pode descartar os dados efemeros do Oracle; o ACR continua existindo e pode gerar custo. Para remover todos os recursos exclusivos desse teste quando nao forem mais necessarios:

```powershell
az group delete --name rg-petcenter-s4-test-20261104
```

Esses comandos de pausa/remocao foram documentados, **nao executados**. A topologia definitiva deve manter o banco separado do redeploy da API.

Referencia tecnica: [grupos multi-container no Azure Container Instances](https://learn.microsoft.com/en-us/azure/container-instances/container-instances-multi-container-yaml).
