#!/usr/bin/env sh
# run-tests.sh - gemelo POSIX de run-tests.ps1. Ejecuta la bateria de
# pruebas/evidencia (app.TestRunner) sobre out/ (ver scripts/compile.sh).
#
# Uso: sh scripts/run-tests.sh

set -eu

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
REPO_ROOT=$(dirname "$SCRIPT_DIR")
OUT_DIR="$REPO_ROOT/out"

if [ ! -f "$OUT_DIR/app/TestRunner.class" ]; then
    echo "No hay clases compiladas en $OUT_DIR." >&2
    echo "Ejecuta primero: sh scripts/compile.sh" >&2
    exit 1
fi

exec java -cp "$OUT_DIR" app.TestRunner
