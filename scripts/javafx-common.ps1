# javafx-common.ps1 - utilidades compartidas por compile-gui.ps1 y run-gui.ps1.
#
# No se ejecuta directamente: se carga con dot-sourcing (`. .\javafx-common.ps1`).

# javafx.animation NO es un modulo: las clases Timeline/KeyFrame viven dentro de
# javafx.graphics. Pedirlo en --add-modules aborta con "module not found".
$JavaFxModules = "javafx.controls,javafx.graphics"

function Resolve-JavaFxLib {
    param([Parameter(Mandatory = $true)][string] $RepoRoot)

    $libDir = Join-Path $RepoRoot "lib"
    $sdk = Get-ChildItem -Path $libDir -Directory -Filter "javafx-sdk-*" -ErrorAction SilentlyContinue |
        Where-Object { Test-Path (Join-Path $_.FullName "lib\javafx.controls.jar") } |
        Sort-Object Name -Descending |
        Select-Object -First 1

    if (-not $sdk) {
        Write-Host ""
        Write-Host "No se encontro el SDK de JavaFX en $libDir." -ForegroundColor Red
        Write-Host "Ejecuta primero:" -ForegroundColor Yellow
        Write-Host "    powershell -ExecutionPolicy Bypass -File scripts\fetch-javafx.ps1"
        Write-Host "(ese script imprime tambien los pasos manuales si la descarga falla)."
        Write-Host ""
        Write-Host "El juego de consola no necesita JavaFX: usa scripts\compile.ps1." -ForegroundColor Cyan
        exit 1
    }
    return (Join-Path $sdk.FullName "lib")
}
