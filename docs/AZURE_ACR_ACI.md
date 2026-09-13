# Azure CLI - ACR + ACI

Esta etapa publica a solucao containerizada do PetCenter no Microsoft Azure usando Azure CLI, Azure Container Registry (ACR) e Azure Container Instances (ACI).

## Recursos Criados Durante a Validacao

- Resource Group: `rg-petcenter-rm564939-devops`
- Azure Container Registry: `acrpetcenterrm564939`
- Login server do ACR: `acrpetcenterrm564939.azurecr.io`
- Azure Container Instance: `aci-petcenter-rm564939`
- DNS publico: `petcenter-rm564939-devops.brazilsouth.azurecontainer.io`
- URL Swagger usada na validacao: `http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080/swagger-ui.html`

## Imagens Publicadas no ACR

- `acrpetcenterrm564939.azurecr.io/petcenter-api:latest`
- `acrpetcenterrm564939.azurecr.io/petcenter-oracle:latest`

A imagem `petcenter-api` contem a API Java/Spring Boot.

A imagem `petcenter-oracle` usa `gvenzl/oracle-free:23-slim` como base e inclui o script SQL do projeto em `/container-entrypoint-initdb.d`, permitindo inicializar o schema `PETCENTER` no container do banco.

## Comandos Executados

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

## Validacao em Nuvem

Comandos usados para verificar o ACI:

```bash
az container show \
  --resource-group rg-petcenter-rm564939-devops \
  --name aci-petcenter-rm564939

az container logs \
  --resource-group rg-petcenter-rm564939-devops \
  --name aci-petcenter-rm564939 \
  --container-name app

az container logs \
  --resource-group rg-petcenter-rm564939-devops \
  --name aci-petcenter-rm564939 \
  --container-name oracle
```

Resultado validado:

- Container `app`: `Running`.
- Container `oracle`: `Running`.
- Swagger publico: HTTP `200`.
- Rota `/`: HTTP `200` com `Challenge PetCenter API funcionando!`.
- Cadastro de usuario via URL publica: OK.
- Login JWT via URL publica: OK.
- Criacao de pet via URL publica: OK.

Tambem ha um roteiro de teste remoto:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test-azure-public.ps1
```

## URL Publica Usada na Validacao

```text
http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080/swagger-ui.html
```

## Observacao de Seguranca

O container da API continua configurado para nao executar como `root/admin`. No Dockerfile, a aplicacao executa com o usuario `spring`.

As credenciais sensiveis foram enviadas ao ACI como `secureValue` no YAML renderizado localmente. O arquivo versionado `azure/container-group.template.yml` contem apenas placeholders.
