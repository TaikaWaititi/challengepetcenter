$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$destination = Join-Path $root '.env'
if (Test-Path -LiteralPath $destination) { throw '.env ja existe; preserve suas credenciais atuais.' }
function New-RandomHex {
    $bytes = New-Object byte[] 24
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
    return ([BitConverter]::ToString($bytes)).Replace('-', '').ToLowerInvariant()
}
$jwtBytes = New-Object byte[] 48
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($jwtBytes) } finally { $rng.Dispose() }
$databasePassword = New-RandomHex
$lines = @(
    "ORACLE_PASSWORD=$(New-RandomHex)",
    'APP_USER=PETCENTER',
    "APP_USER_PASSWORD=$databasePassword",
    'SPRING_DATASOURCE_URL=jdbc:oracle:thin:@oracle:1521/FREEPDB1',
    'SPRING_DATASOURCE_USERNAME=PETCENTER',
    "SPRING_DATASOURCE_PASSWORD=$databasePassword",
    "JWT_SECRET=$([Convert]::ToBase64String($jwtBytes))"
)
[IO.File]::WriteAllLines($destination, $lines, (New-Object Text.UTF8Encoding $false))
Write-Host '.env criado com valores aleatorios. Nao versione nem compartilhe o arquivo.'
