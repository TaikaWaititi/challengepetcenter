$ErrorActionPreference = "Stop"

$BaseUrl = if ($env:BASE_URL) { $env:BASE_URL } else { "http://localhost:8080" }
$Stamp = Get-Date -Format "yyyyMMddHHmmss"

function Assert-Status {
    param(
        [string]$Name,
        [int]$Actual,
        [int[]]$Expected
    )

    if ($Expected -notcontains $Actual) {
        throw "$Name retornou HTTP $Actual. Esperado: $($Expected -join ', ')."
    }
}

function Invoke-Json {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers,
        [object]$Body
    )

    $params = @{
        Method = $Method
        Uri = $Uri
        ContentType = "application/json"
    }

    if ($Headers) {
        $params.Headers = $Headers
    }

    if ($null -ne $Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 10)
    }

    Invoke-RestMethod @params
}

function Wait-HttpOk {
    param(
        [string]$Uri,
        [int]$Retries = 30,
        [int]$DelaySeconds = 3
    )

    for ($i = 1; $i -le $Retries; $i++) {
        try {
            $response = Invoke-WebRequest -Uri $Uri -MaximumRedirection 5 -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                return $response
            }
        } catch {
            if ($i -eq $Retries) {
                throw
            }
        }

        Start-Sleep -Seconds $DelaySeconds
    }

    throw "Servico nao respondeu HTTP 200 em $Uri."
}

Write-Host "Testando API em $BaseUrl"

Write-Host "Evidencia Docker: containers, usuario do app e tamanho da imagem"
docker compose ps
docker compose exec -T app id
docker image ls challengepetcenterjava-app --format "Imagem {{.Repository}}:{{.Tag}} - tamanho {{.Size}} - id {{.ID}}"

Write-Host "Aguardando Swagger responder"
$swagger = Wait-HttpOk -Uri "$BaseUrl/swagger-ui.html"
Assert-Status "Swagger" $swagger.StatusCode @(200)
Write-Host "Swagger HTTP $($swagger.StatusCode)"

$email = "devops.$Stamp@petcenter.com"
$senha = "123456"

$userBody = @{
    nome = "Tutor DevOps"
    email = $email
    senha = $senha
    telefone = "11 90000-0001"
    tipoUsuario = "TUTOR"
}

Write-Host "POST /api/users - cadastro publico"
$user = Invoke-Json -Method Post -Uri "$BaseUrl/api/users" -Body $userBody

$loginBody = @{
    email = $email
    senha = $senha
}

Write-Host "POST /api/auth/login - gerando JWT"
$login = Invoke-Json -Method Post -Uri "$BaseUrl/api/auth/login" -Body $loginBody
$headers = @{ Authorization = "Bearer $($login.token)" }

$petBody = @{
    nome = "Bolt DevOps"
    especie = "Cachorro"
    raca = "Golden Retriever"
    dataNascimento = "2020-05-10"
    observacoes = "Pet usado no teste de DevOps"
}

Write-Host "POST /api/pets - inclusao"
$pet = Invoke-Json -Method Post -Uri "$BaseUrl/api/pets" -Headers $headers -Body $petBody

Write-Host "GET /api/pets/{id} - consulta"
$petConsultado = Invoke-Json -Method Get -Uri "$BaseUrl/api/pets/$($pet.id)" -Headers $headers

$petAtualizadoBody = @{
    nome = "Bolt DevOps Atualizado"
    especie = "Cachorro"
    raca = "Golden Retriever"
    dataNascimento = "2020-05-10"
    observacoes = "Pet atualizado no teste de CRUD pre-Azure"
}

Write-Host "PUT /api/pets/{id} - alteracao"
$petAtualizado = Invoke-Json -Method Put -Uri "$BaseUrl/api/pets/$($pet.id)" -Headers $headers -Body $petAtualizadoBody

$diarioBody = @{
    petId = $pet.id
    data = "2026-05-23"
    resumo = "Pet apresentou boa evolucao no teste de continuidade do cuidado"
    humorGeral = "Feliz"
    status = "COMPLETO"
}

Write-Host "POST /api/diarioentradas - inclusao"
$diario = Invoke-Json -Method Post -Uri "$BaseUrl/api/diarioentradas" -Headers $headers -Body $diarioBody

Write-Host "GET /api/diarioentradas/{id} - consulta"
$diarioConsultado = Invoke-Json -Method Get -Uri "$BaseUrl/api/diarioentradas/$($diario.id)" -Headers $headers

$diarioAtualizadoBody = @{
    petId = $pet.id
    data = "2026-05-23"
    resumo = "Entrada atualizada no teste de CRUD completo antes do Azure"
    humorGeral = "Calmo"
    status = "COMPLETO"
}

Write-Host "PUT /api/diarioentradas/{id} - alteracao"
$diarioAtualizado = Invoke-Json -Method Put -Uri "$BaseUrl/api/diarioentradas/$($diario.id)" -Headers $headers -Body $diarioAtualizadoBody

$registroBody = @{
    entradaId = $diario.id
    tipo = "ALIMENTACAO"
    subtipo = "Racao"
    valor = 250.0
    unidade = "gramas"
    nota = "Comeu normalmente durante o teste"
}

Write-Host "POST /api/registros - inclusao"
$registro = Invoke-Json -Method Post -Uri "$BaseUrl/api/registros" -Headers $headers -Body $registroBody

Write-Host "GET /api/registros/{id} - consulta"
$registroConsultado = Invoke-Json -Method Get -Uri "$BaseUrl/api/registros/$($registro.id)" -Headers $headers

$registroAtualizadoBody = @{
    entradaId = $diario.id
    tipo = "ALIMENTACAO"
    subtipo = "Racao umida"
    valor = 180.0
    unidade = "gramas"
    nota = "Registro atualizado no teste de CRUD completo"
}

Write-Host "PUT /api/registros/{id} - alteracao"
$registroAtualizado = Invoke-Json -Method Put -Uri "$BaseUrl/api/registros/$($registro.id)" -Headers $headers -Body $registroAtualizadoBody

Write-Host "POST /api/users - cadastro publico de veterinario"
$vetEmail = "vet.devops.$Stamp@petcenter.com"
$vetUserBody = @{
    nome = "Veterinario DevOps"
    email = $vetEmail
    senha = $senha
    telefone = "11 90000-0002"
    tipoUsuario = "VETERINARIO"
}
$vetUser = Invoke-Json -Method Post -Uri "$BaseUrl/api/users" -Body $vetUserBody

Write-Host "POST /api/auth/login - JWT veterinario"
$vetLoginBody = @{
    email = $vetEmail
    senha = $senha
}
$vetLogin = Invoke-Json -Method Post -Uri "$BaseUrl/api/auth/login" -Body $vetLoginBody
$vetHeaders = @{ Authorization = "Bearer $($vetLogin.token)" }

Write-Host "POST /api/veterinarios - perfil veterinario"
$veterinarioBody = @{
    crmv = "DEV$($Stamp.Substring(8,6))"
    especialidade = "Clinica Geral"
    descricao = "Veterinario usado no teste de alertas da etapa DevOps"
}
$veterinario = Invoke-Json -Method Post -Uri "$BaseUrl/api/veterinarios" -Headers $vetHeaders -Body $veterinarioBody

Write-Host "POST /api/alertas - modulo novo da versao Java"
$alertaBody = @{
    petId = 1
    tipo = "REMEDIO"
    titulo = "Alerta DevOps"
    descricao = "Alerta criado para validar alinhamento com a nova versao Java"
    dataInicio = "2026-09-12T08:00:00"
    frequenciaHoras = 12
    dataFim = "2026-09-20T08:00:00"
}
$alerta = Invoke-Json -Method Post -Uri "$BaseUrl/api/alertas" -Headers $vetHeaders -Body $alertaBody

Write-Host "GET /api/alertas/{id} - consulta alerta"
$alertaConsultado = Invoke-Json -Method Get -Uri "$BaseUrl/api/alertas/$($alerta.id)" -Headers $vetHeaders

Write-Host "PATCH /api/alertas/{id}/desativar - desativacao alerta"
$alertaDesativado = Invoke-Json -Method Patch -Uri "$BaseUrl/api/alertas/$($alerta.id)/desativar" -Headers $vetHeaders

Write-Host "DELETE /api/registros/{id} - exclusao"
$deleteRegistro = Invoke-WebRequest -Method Delete -Uri "$BaseUrl/api/registros/$($registro.id)" -Headers $headers -UseBasicParsing
Assert-Status "DELETE registro" $deleteRegistro.StatusCode @(204)

Write-Host "DELETE /api/diarioentradas/{id} - exclusao"
$deleteDiario = Invoke-WebRequest -Method Delete -Uri "$BaseUrl/api/diarioentradas/$($diario.id)" -Headers $headers -UseBasicParsing
Assert-Status "DELETE diario" $deleteDiario.StatusCode @(204)

Write-Host "DELETE /api/pets/{id} - exclusao auxiliar"
$deletePet = Invoke-WebRequest -Method Delete -Uri "$BaseUrl/api/pets/$($pet.id)" -Headers $headers -UseBasicParsing
Assert-Status "DELETE pet" $deletePet.StatusCode @(204)

$result = [ordered]@{
    status = "OK"
    timestamp = (Get-Date).ToString("s")
    swaggerHttp = $swagger.StatusCode
    tabelasRelacionadasComCrudCompleto = @("pets", "diario_entradas", "registros")
    moduloAlertasDaNovaVersaoJava = @{
        veterinario = $veterinario
        alertaCriado = $alerta
        alertaConsultado = $alertaConsultado
        alertaDesativado = $alertaDesativado
    }
    user = $user
    pet = @{
        criado = $pet
        consultado = $petConsultado
        atualizado = $petAtualizado
        deleteHttp = $deletePet.StatusCode
    }
    diario = @{
        criado = $diario
        consultado = $diarioConsultado
        atualizado = $diarioAtualizado
        deleteHttp = $deleteDiario.StatusCode
    }
    registro = @{
        criado = $registro
        consultado = $registroConsultado
        atualizado = $registroAtualizado
        deleteHttp = $deleteRegistro.StatusCode
    }
}

$result | ConvertTo-Json -Depth 20

Write-Host "Evidencia no banco Oracle: SELECT nas tabelas principais"
docker compose exec -T oracle sqlplus -s PETCENTER/petcenter@FREEPDB1 "@/database/select-evidencias.sql"
