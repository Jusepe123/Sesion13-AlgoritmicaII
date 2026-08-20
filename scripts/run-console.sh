#!/usr/bin/env sh
# run-console.sh - gemelo POSIX de run-console.ps1. Ejecuta el juego
# interactivo de consola (app.Main) sobre out/ (ver scripts/compile.sh).
#
# Uso: sh scripts/run-console.sh
# Uso con entrada redirigida (evidencia reproducible):
#   sh scripts/run-console.sh < scripts/inputs/input-modo1-nivel1.txt

set -eu

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
REPO_ROOT=$(dirname "$SCRIPT_DIR")
OUT_DIR="$REPO_ROOT/out"

if [ ! -f "$OUT_DIR/app/Main.class" ]; then
    echo "No hay clases compiladas en $OUT_DIR." >&2
    echo "Ejecuta primero: sh scripts/compile.sh" >&2
    exit 1
fi

exec java -cp "$OUT_DIR" app.Main
