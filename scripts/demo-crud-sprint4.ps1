param(
    [Parameter(Mandatory = $true)][string]$BaseUrl,
    [switch]$NonInteractive,
    [ValidateRange(1, 600)][int]$TimeoutSec = 120
)
$ErrorActionPreference = 'Stop'
$BaseUrl = $BaseUrl.TrimEnd('/')
if ($BaseUrl -notmatch '^https?://') { throw 'Informe uma URL HTTP(S) valida.' }
function Invoke-Api {
    param([string]$Method, [string]$Path, [object]$Body, [string]$Token)
    Write-Host "Executando $Method $Path"
    $arguments = @{ Method = $Method; Uri = "$BaseUrl$Path"; ContentType = 'application/json'; TimeoutSec = $TimeoutSec }
    if ($null -ne $Body) { $arguments.Body = $Body | ConvertTo-Json -Compress }
    if ($Token) { $arguments.Headers = @{ Authorization = "Bearer $Token" } }
    Invoke-RestMethod @arguments
}
function Show-Evidence {
    param([string]$Step, [long]$PetId)
    Write-Host "`n$Step - execute no cliente SQL conectado ao banco em nuvem:"
    Write-Host "SELECT id, user_id, nome, especie FROM pets WHERE id = $PetId;"
    if (-not $NonInteractive) {
        Read-Host 'Mostre o SELECT e explique o resultado na gravacao; pressione Enter para continuar' | Out-Null
    }
}
$email = 'sprint4.' + [Guid]::NewGuid().ToString('N') + '@example.test'
$password = [Guid]::NewGuid().ToString('N').Substring(0, 8)
$user = Invoke-Api POST '/api/users' @{
    nome = 'Tutor Sprint Quatro'; email = $email; senha = $password
    telefone = '11900000000'; tipoUsuario = 'TUTOR'
}
$login = Invoke-Api POST '/api/auth/login' @{ email = $email; senha = $password }
$token = $login.token
if (-not $token) { throw 'Login nao retornou token.' }
$body = @{ nome = 'Luna Sprint Quatro'; especie = 'Cachorro'; raca = 'SRD'; dataNascimento = '2021-01-10'; observacoes = 'Demonstracao CI/CD' }
$pet = Invoke-Api POST '/api/pets' $body $token
if (-not $pet.id) { throw 'Inclusao nao retornou ID.' }
Show-Evidence '1. Insercao: deve existir uma linha com o nome original' $pet.id
$read = Invoke-Api GET "/api/pets/$($pet.id)" $null $token
if ($read.nome -ne $body.nome) { throw 'Consulta retornou nome diferente do cadastrado.' }
Write-Host "Consulta da API: id=$($read.id), nome=$($read.nome)"
Show-Evidence '2. Consulta: compare o retorno da API com o SELECT' $pet.id
$body.nome = 'Luna Atualizada Sprint Quatro'
$updated = Invoke-Api PUT "/api/pets/$($pet.id)" $body $token
if ($updated.nome -ne $body.nome) { throw 'Atualizacao nao foi confirmada pela API.' }
Show-Evidence '3. Atualizacao: SELECT deve retornar o novo nome' $pet.id
Invoke-Api DELETE "/api/pets/$($pet.id)" $null $token | Out-Null
Show-Evidence '4. Exclusao: SELECT nao deve retornar linhas' $pet.id
Invoke-Api DELETE "/api/users/$($user.id)" $null $token | Out-Null
Write-Host 'CRUD concluido e usuario de demonstracao removido. Os SELECTs devem ser executados e filmados no cliente SQL.'
