$ErrorActionPreference = "Stop"

$BaseUrl = if ($env:BASE_URL) { $env:BASE_URL } else { "http://localhost:8080" }
$Stamp = Get-Date -Format "yyyyMMddHHmmss"

Write-Host "Testando API em $BaseUrl"

$user1Body = @{
    nome = "Bruno DevOps"
    email = "bruno.devops.$Stamp@petcenter.com"
    senha = "123456"
    telefone = "11 90000-0001"
    tipoUsuario = "Tutor"
} | ConvertTo-Json

$user2Body = @{
    nome = "Mariana DevOps"
    email = "mariana.devops.$Stamp@petcenter.com"
    senha = "123456"
    telefone = "11 90000-0002"
    tipoUsuario = "Tutor"
} | ConvertTo-Json

Write-Host "POST /api/users - usuario 1"
$user1 = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/users" -ContentType "application/json" -Body $user1Body

Write-Host "POST /api/users - usuario 2"
$user2 = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/users" -ContentType "application/json" -Body $user2Body

Write-Host "GET /api/users"
$usersAfterPost = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/users"

$user1PutBody = @{
    nome = "Bruno DevOps Atualizado"
    email = $user1.email
    senha = "123456"
    telefone = "11 91111-1111"
    tipoUsuario = "Tutor"
} | ConvertTo-Json

Write-Host "PUT /api/users/$($user1.id)"
$user1Updated = Invoke-RestMethod -Method Put -Uri "$BaseUrl/api/users/$($user1.id)" -ContentType "application/json" -Body $user1PutBody

Write-Host "DELETE /api/users/$($user2.id)"
Invoke-RestMethod -Method Delete -Uri "$BaseUrl/api/users/$($user2.id)" | Out-Null

$petBody = @{
    userId = $user1.id
    nome = "Bolt DevOps"
    especie = "Cachorro"
    raca = "Golden Retriever"
    dataNascimento = "2020-05-10"
    observacoes = "Pet usado no teste de DevOps"
} | ConvertTo-Json

Write-Host "POST /api/pets"
$pet = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/pets" -ContentType "application/json" -Body $petBody

$diarioBody = @{
    petId = $pet.id
    data = "2026-05-23"
    resumo = "Pet apresentou boa evolucao no teste de continuidade do cuidado"
    humorGeral = "Feliz"
    status = "ATIVO"
} | ConvertTo-Json

Write-Host "POST /api/diarioentradas"
$diario = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/diarioentradas" -ContentType "application/json" -Body $diarioBody

$registroBody = @{
    entradaId = $diario.id
    tipo = "Alimentacao"
    subtipo = "Racao"
    valor = 250.0
    unidade = "gramas"
    nota = "Comeu normalmente durante o teste"
} | ConvertTo-Json

Write-Host "POST /api/registros"
$registro = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/registros" -ContentType "application/json" -Body $registroBody

$result = [ordered]@{
    status = "OK"
    timestamp = (Get-Date).ToString("s")
    users_after_post_count = if ($null -ne $usersAfterPost.totalElements) { $usersAfterPost.totalElements } elseif ($null -ne $usersAfterPost.content) { @($usersAfterPost.content).Count } else { @($usersAfterPost).Count }
    updated_user = $user1Updated
    deleted_user_id = $user2.id
    pet = $pet
    diario = $diario
    registro = $registro
}

$result | ConvertTo-Json -Depth 10
