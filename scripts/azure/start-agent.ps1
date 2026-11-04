param([string]$Organization = 'https://dev.azure.com/Sprint4fiap')
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$directory = Join-Path $root 'tmp/agent'
New-Item -ItemType Directory -Force -Path $directory | Out-Null
$tokenFile = Join-Path $directory 'registration.secrets'
$existing = docker ps -a --filter 'name=^/petcenter-azuredevops-agent$' --format '{{.Names}}'
if ($existing) {
    docker start petcenter-azuredevops-agent | Out-Null
    Write-Host 'Agente existente iniciado; confira o estado no Azure DevOps.'
    exit
}
$token = az account get-access-token --resource 499b84ac-1321-427f-aa17-267ca6975798 --query accessToken -o tsv
if ($LASTEXITCODE -ne 0) { throw 'Autentique-se no Azure CLI com a conta administradora do pool.' }
[IO.File]::WriteAllText($tokenFile, $token.Trim(), (New-Object Text.UTF8Encoding $false))
docker build -t petcenter-azuredevops-agent (Join-Path $root 'azure/agent')
if ($LASTEXITCODE -ne 0) { throw 'Falha no build do agente.' }
docker network inspect petcenter-ci *> $null
if ($LASTEXITCODE -ne 0) { docker network create petcenter-ci | Out-Null }
docker run -d --name petcenter-azuredevops-agent --restart unless-stopped --network petcenter-ci --mount "type=bind,source=$tokenFile,target=/run/secrets/registration-token,readonly" -v /var/run/docker.sock:/var/run/docker.sock -v petcenter-ado-agent:/azp -e "AZP_URL=$Organization" -e AZP_POOL=PetCenter-Linux -e AZP_AGENT_NAME=PetCenter-Codex-Linux petcenter-azuredevops-agent
if ($LASTEXITCODE -ne 0) { throw 'Falha ao iniciar o agente.' }
Write-Host 'Agente iniciado. O pool PetCenter-Linux deve existir e estar autorizado para as pipelines.'
