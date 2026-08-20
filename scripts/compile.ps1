# compile.ps1 - compila el nucleo calificado (src/) hacia out/ con javac puro.
#
# CERO JavaFX: no toca el classpath ni el module-path con nada de lib/. Esto
# es exclusivamente el nucleo de consola (model/dsu/algorithm/game/console/app).
# Para el bonus grafico usar scripts\compile-gui.ps1 (compila src/ + src-gui/
# en out-gui/, aparte).
#
# Uso: powershell -ExecutionPolicy Bypass -File scripts\compile.ps1

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$SrcDir   = Join-Path $RepoRoot "src"
$OutDir   = Join-Path $RepoRoot "out"

if (Test-Path $OutDir) { Remove-Item $OutDir -Recurse -Force }
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

$sources = Get-ChildItem -Path $SrcDir -Recurse -Filter *.java | ForEach-Object { $_.FullName }

Write-Host "Compilando $($sources.Count) archivos .java (src/) en $OutDir ..." -ForegroundColor Cyan

$argsFile = Join-Path $env:TEMP "networkbuilder-sources.txt"
$lines = $sources | ForEach-Object { '"' + ($_ -replace '\\', '/') + '"' }
[System.IO.File]::WriteAllLines($argsFile, $lines, (New-Object System.Text.UTF8Encoding($false)))

& javac -d $OutDir "@$argsFile"
$code = $LASTEXITCODE
Remove-Item $argsFile -Force -ErrorAction SilentlyContinue

if ($code -ne 0) {
    Write-Host "Fallo la compilacion (exit $code)." -ForegroundColor Red
    exit $code
}

Write-Host "OK: compilacion exitosa -> $OutDir" -ForegroundColor Green
Write-Host "Siguiente paso: powershell -ExecutionPolicy Bypass -File scripts\run-console.ps1"
