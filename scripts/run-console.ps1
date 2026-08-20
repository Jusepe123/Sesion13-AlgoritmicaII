# run-console.ps1 - ejecuta el juego interactivo de consola (app.Main) sobre
# las clases ya compiladas en out/ (ver scripts\compile.ps1). Cero JavaFX.
#
# Uso: powershell -ExecutionPolicy Bypass -File scripts\run-console.ps1
# Uso con entrada redirigida (evidencia reproducible):
#   powershell -ExecutionPolicy Bypass -File scripts\run-console.ps1 < scripts\inputs\input-modo1-nivel1.txt

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$OutDir   = Join-Path $RepoRoot "out"

if (-not (Test-Path (Join-Path $OutDir "app\Main.class"))) {
    Write-Host "No hay clases compiladas en $OutDir." -ForegroundColor Red
    Write-Host "Ejecuta primero: powershell -ExecutionPolicy Bypass -File scripts\compile.ps1"
    exit 1
}

& java -cp $OutDir app.Main
exit $LASTEXITCODE
