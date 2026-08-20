# run-gui.ps1 - abre la ventana del bonus grafico desde out-gui/.
#
# Uso: powershell -ExecutionPolicy Bypass -File scripts\run-gui.ps1

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$OutDir   = Join-Path $RepoRoot "out-gui"

. (Join-Path $PSScriptRoot "javafx-common.ps1")
$fxLib = Resolve-JavaFxLib -RepoRoot $RepoRoot

if (-not (Test-Path (Join-Path $OutDir "gui\GameApp.class"))) {
    Write-Host "No hay clases compiladas en $OutDir." -ForegroundColor Red
    Write-Host "Ejecuta primero: powershell -ExecutionPolicy Bypass -File scripts\compile-gui.ps1" -ForegroundColor Yellow
    exit 1
}

Write-Host "Abriendo NetworkBuilder (JavaFX) ..." -ForegroundColor Cyan
& java --module-path $fxLib --add-modules $JavaFxModules -cp $OutDir gui.GameApp
exit $LASTEXITCODE
