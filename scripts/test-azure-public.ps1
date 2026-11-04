param([Parameter(Mandatory = $true)][string]$BaseUrl)
$ErrorActionPreference = 'Stop'
# URL explicita para evitar criar dados acidentalmente em um ambiente antigo.
& (Join-Path $PSScriptRoot 'demo-crud-sprint4.ps1') -BaseUrl $BaseUrl
