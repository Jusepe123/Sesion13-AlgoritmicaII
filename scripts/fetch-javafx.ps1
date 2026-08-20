# fetch-javafx.ps1 - descarga el SDK de JavaFX 21.x (Windows x64) dentro de lib/.
#
# Este script SOLO afecta al bonus grafico (Tarea 5). El nucleo de consola
# (Tareas 1-4) se compila con `javac` puro y no depende de JavaFX en absoluto:
# si esta descarga falla, el entregable calificado sigue funcionando.
#
# Uso:
#   powershell -ExecutionPolicy Bypass -File scripts\fetch-javafx.ps1
#   powershell -ExecutionPolicy Bypass -File scripts\fetch-javafx.ps1 -Version 21.0.5

[CmdletBinding()]
param(
    [string] $Version = "",
    [switch] $Force
)

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$LibDir   = Join-Path $RepoRoot "lib"

# Se prueban de la mas nueva a la mas vieja. Todas son JavaFX 21.x (LTS),
# la linea compatible con JDK 21.
[string[]] $DefaultCandidates = @("21.0.9", "21.0.8", "21.0.7", "21.0.6", "21.0.5")
$PreferredVersion = $DefaultCandidates[0]

# El tipo [string[]] es obligatorio: sin el, PowerShell desenvuelve el arreglo
# de un solo elemento a una cadena suelta y luego $Candidates[0] devuelve su
# PRIMER CARACTER ("9" en vez de "99.99.99").
[string[]] $Candidates = if ($Version) { @($Version) } else { $DefaultCandidates }

function Get-InstalledSdk {
    if (-not (Test-Path $LibDir)) { return $null }
    Get-ChildItem -Path $LibDir -Directory -Filter "javafx-sdk-*" -ErrorAction SilentlyContinue |
        Where-Object { Test-Path (Join-Path $_.FullName "lib\javafx.controls.jar") } |
        Sort-Object Name -Descending |
        Select-Object -First 1
}

function Write-ManualInstructions {
    param([string] $Reason)

    # Siempre se recomienda la version fijada por defecto, aunque el intento
    # fallido haya sido con un -Version que el usuario escribio mal.
    $preferred = $PreferredVersion
    Write-Host ""
    Write-Host "======================================================================" -ForegroundColor Yellow
    Write-Host " No se pudo obtener el SDK de JavaFX automaticamente." -ForegroundColor Yellow
    Write-Host " Motivo: $Reason" -ForegroundColor Yellow
    Write-Host "======================================================================" -ForegroundColor Yellow
    Write-Host ""
    Write-Host " Pasos manuales (5 minutos):"
    Write-Host ""
    Write-Host "   1. Descarga el SDK de JavaFX 21 para Windows x64 desde:"
    Write-Host "        https://gluonhq.com/products/javafx/"
    Write-Host "      (elige: JavaFX $preferred / Windows / x64 / SDK)"
    Write-Host "      Enlace directo:"
    Write-Host "        https://download2.gluonhq.com/openjfx/$preferred/openjfx-${preferred}_windows-x64_bin-sdk.zip"
    Write-Host ""
    Write-Host "   2. Descomprime el zip. Adentro trae una carpeta 'javafx-sdk-$preferred'."
    Write-Host ""
    Write-Host "   3. Copia esa carpeta a:"
    Write-Host "        $LibDir"
    Write-Host ""
    Write-Host "   4. Verifica que exista exactamente este archivo:"
    Write-Host "        $LibDir\javafx-sdk-$preferred\lib\javafx.controls.jar"
    Write-Host ""
    Write-Host "   5. Luego ejecuta:"
    Write-Host "        powershell -ExecutionPolicy Bypass -File scripts\compile-gui.ps1"
    Write-Host "        powershell -ExecutionPolicy Bypass -File scripts\run-gui.ps1"
    Write-Host ""
    Write-Host " Recordatorio: el juego de consola NO necesita JavaFX." -ForegroundColor Cyan
    Write-Host " Sigue funcionando con scripts\compile.ps1 y scripts\run-console.ps1." -ForegroundColor Cyan
    Write-Host ""
}

$existing = Get-InstalledSdk
if ($existing -and -not $Force) {
    Write-Host "JavaFX ya esta instalado: $($existing.FullName)" -ForegroundColor Green
    Write-Host "  (usa -Force para volver a descargarlo)"
    exit 0
}

New-Item -ItemType Directory -Force -Path $LibDir | Out-Null

# TLS 1.2 explicito: Windows PowerShell 5.1 a veces negocia TLS 1.0 y falla.
try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12 } catch { }

$lastError = "desconocido"
foreach ($v in $Candidates) {
    $url = "https://download2.gluonhq.com/openjfx/$v/openjfx-${v}_windows-x64_bin-sdk.zip"
    $zip = Join-Path $LibDir "openjfx-${v}_windows-x64_bin-sdk.zip"

    Write-Host "Descargando JavaFX $v ..." -ForegroundColor Cyan
    Write-Host "  $url"
    try {
        $progressPreferenceBackup = $ProgressPreference
        $ProgressPreference = "SilentlyContinue"   # acelera mucho Invoke-WebRequest
        Invoke-WebRequest -Uri $url -OutFile $zip -UseBasicParsing -TimeoutSec 600
        $ProgressPreference = $progressPreferenceBackup

        Write-Host "Descomprimiendo en $LibDir ..." -ForegroundColor Cyan
        Expand-Archive -Path $zip -DestinationPath $LibDir -Force
        Remove-Item $zip -Force -ErrorAction SilentlyContinue

        # Se verifica la carpeta de ESTA version, no "la mas nueva que haya":
        # con -Force sobre una instalacion previa eso reportaria otra version.
        $sdkDir = Join-Path $LibDir "javafx-sdk-$v"
        $jar    = Join-Path $sdkDir "lib\javafx.controls.jar"
        if (-not (Test-Path $jar)) {
            $lastError = "el zip se descomprimio pero no aparecio $jar"
            Write-Host "  Aviso: $lastError" -ForegroundColor Yellow
            continue
        }

        Write-Host ""
        Write-Host "OK: JavaFX $v instalado." -ForegroundColor Green
        Write-Host "  SDK  : $sdkDir"
        Write-Host "  Jar  : $jar"
        Write-Host ""
        Write-Host "Siguiente paso: powershell -ExecutionPolicy Bypass -File scripts\compile-gui.ps1"
        exit 0
    }
    catch {
        $lastError = $_.Exception.Message
        Write-Host "  Fallo con $v : $lastError" -ForegroundColor Yellow
        Remove-Item $zip -Force -ErrorAction SilentlyContinue
    }
}

Write-ManualInstructions -Reason $lastError
exit 1
