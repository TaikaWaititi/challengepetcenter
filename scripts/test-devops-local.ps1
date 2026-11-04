# Compatibilidade com o comando anterior; roteiro de CRUD com pausas para SELECT.
$ErrorActionPreference = 'Stop'
$base = if ($env:BASE_URL) { $env:BASE_URL } else { 'http://localhost:8080' }
& (Join-Path $PSScriptRoot 'demo-crud-sprint4.ps1') -BaseUrl $base
