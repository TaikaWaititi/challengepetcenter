$ErrorActionPreference = "Stop"

$BaseUrl = if ($env:BASE_URL) {
    $env:BASE_URL
} else {
    "http://petcenter-rm564939-devops.brazilsouth.azurecontainer.io:8080"
}

$Stamp = Get-Date -Format "yyyyMMddHHmmss"
$email = "azure.$Stamp@petcenter.com"
$senha = "123456"

Write-Host "Testando API publicada em $BaseUrl"

$swagger = Invoke-WebRequest -Uri "$BaseUrl/swagger-ui.html" -MaximumRedirection 5 -UseBasicParsing -TimeoutSec 30
$homeResponse = Invoke-WebRequest -Uri "$BaseUrl/" -UseBasicParsing -TimeoutSec 30

$userBody = @{
    nome = "Tutor Azure"
    email = $email
    senha = $senha
    telefone = "11 90000-1000"
    tipoUsuario = "TUTOR"
} | ConvertTo-Json

$user = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/users" -ContentType "application/json" -Body $userBody

$loginBody = @{
    email = $email
    senha = $senha
} | ConvertTo-Json

$login = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/login" -ContentType "application/json" -Body $loginBody
$headers = @{ Authorization = "Bearer $($login.token)" }

$petBody = @{
    nome = "Azure Pet"
    especie = "Cachorro"
    raca = "SRD"
    dataNascimento = "2021-01-10"
    observacoes = "Teste Azure ACI"
} | ConvertTo-Json

$pet = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/pets" -Headers $headers -ContentType "application/json" -Body $petBody

[ordered]@{
    status = "OK"
    baseUrl = $BaseUrl
    swaggerHttp = $swagger.StatusCode
    homeHttp = $homeResponse.StatusCode
    home = $homeResponse.Content
    userId = $user.id
    petId = $pet.id
    petNome = $pet.nome
} | ConvertTo-Json -Depth 5
