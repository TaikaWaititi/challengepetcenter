param(
    [Parameter(Mandatory = $true)][string]$Query,
    [string]$ResourceGroup = 'rg-petcenter-rm564939-sprint4',
    [string]$VmName = 'vm-petcenter-oracle'
)
$ErrorActionPreference = 'Stop'
if ($Query.TrimStart() -notmatch '^(?i:SELECT)\s') { throw 'Este roteiro aceita somente uma consulta SELECT.' }
if ($Query.TrimEnd(';', ' ', "`r", "`n").Contains(';')) { throw 'Use somente uma consulta por chamada.' }
$sql = @"
WHENEVER SQLERROR EXIT SQL.SQLCODE
SET PAGESIZE 100
SET LINESIZE 220
SET FEEDBACK ON
ALTER SESSION SET CONTAINER=FREEPDB1;
ALTER SESSION SET CURRENT_SCHEMA=PETCENTER;
$($Query.TrimEnd(';', ' ', "`r", "`n"));
EXIT
"@
$encoded = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($sql))
$tempDirectory = Join-Path (Split-Path -Parent $PSScriptRoot) '../tmp'
New-Item -ItemType Directory -Force -Path $tempDirectory | Out-Null
$temp = Join-Path $tempDirectory ('select-' + [Guid]::NewGuid().ToString('N') + '.sh')
$script = "set -e`nprintf %s $encoded | base64 -d | docker exec -i petcenter-oracle sqlplus -s / as sysdba`n"
[IO.File]::WriteAllText($temp, $script, (New-Object Text.UTF8Encoding $false))
try {
    Write-Host "Consulta no Oracle da Azure: $Query"
    az vm run-command invoke --resource-group $ResourceGroup --name $VmName --command-id RunShellScript --scripts "@$temp" --query 'value[].message' -o tsv
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao consultar o Oracle na VM.' }
} finally {
    Remove-Item -LiteralPath $temp -ErrorAction SilentlyContinue
}
