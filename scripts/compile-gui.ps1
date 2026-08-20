# compile-gui.ps1 - compila el nucleo (src/) MAS el bonus grafico (src-gui/) en out-gui/.
#
# Esto NO reemplaza la compilacion calificada. El nucleo de consola se sigue
# compilando con `javac` puro hacia out/ (scripts\compile.ps1) sin ninguna
# referencia a JavaFX. Este script produce un arbol de clases aparte, out-gui/,
# solo para poder abrir la ventana del bonus.
#
# Uso: powershell -ExecutionPolicy Bypass -File scripts\compile-gui.ps1

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$OutDir   = Join-Path $RepoRoot "out-gui"

. (Join-Path $PSScriptRoot "javafx-common.ps1")
$fxLib = Resolve-JavaFxLib -RepoRoot $RepoRoot

if (Test-Path $OutDir) { Remove-Item $OutDir -Recurse -Force }
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

$sources = @(
    Get-ChildItem -Path (Join-Path $RepoRoot "src")     -Recurse -Filter *.java
    Get-ChildItem -Path (Join-Path $RepoRoot "src-gui") -Recurse -Filter *.java
) | ForEach-Object { $_.FullName }

Write-Host "Compilando $($sources.Count) archivos .java en $OutDir ..." -ForegroundColor Cyan
Write-Host "  module-path: $fxLib"

# El argfile de javac usa '\' como caracter de escape y separa por espacios:
# hay que pasar las rutas con '/' y entrecomilladas, y sin BOM (un BOM se lee
# como parte del primer argumento y produce "invalid flag: ?C:\...").
$argsFile = Join-Path $env:TEMP "networkbuilder-gui-sources.txt"
$lines = $sources | ForEach-Object { '"' + ($_ -replace '\\', '/') + '"' }
[System.IO.File]::WriteAllLines($argsFile, $lines, (New-Object System.Text.UTF8Encoding($false)))

& javac -d $OutDir --module-path $fxLib --add-modules $JavaFxModules "@$argsFile"
$code = $LASTEXITCODE
Remove-Item $argsFile -Force -ErrorAction SilentlyContinue

if ($code -ne 0) {
    Write-Host "Fallo la compilacion (exit $code)." -ForegroundColor Red
    exit $code
}

Write-Host "OK: compilacion exitosa -> $OutDir" -ForegroundColor Green
Write-Host "Siguiente paso: powershell -ExecutionPolicy Bypass -File scripts\run-gui.ps1"
