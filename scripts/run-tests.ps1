# run-tests.ps1 - ejecuta la bateria de pruebas/evidencia (app.TestRunner)
# sobre las clases ya compiladas en out/ (ver scripts\compile.ps1). Cero JavaFX.
#
# Uso: powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$OutDir   = Join-Path $RepoRoot "out"

if (-not (Test-Path (Join-Path $OutDir "app\TestRunner.class"))) {
    Write-Host "No hay clases compiladas en $OutDir." -ForegroundColor Red
    Write-Host "Ejecuta primero: powershell -ExecutionPolicy Bypass -File scripts\compile.ps1"
    exit 1
}

& java -cp $OutDir app.TestRunner
exit $LASTEXITCODE
