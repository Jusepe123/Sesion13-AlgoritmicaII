#!/usr/bin/env sh
# run-gui.sh - gemelo POSIX de run-gui.ps1. Abre la ventana del bonus grafico.
#
# Uso: sh scripts/run-gui.sh

set -eu

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
REPO_ROOT=$(dirname "$SCRIPT_DIR")
OUT_DIR="$REPO_ROOT/out-gui"

# shellcheck source=./javafx-common.sh
. "$SCRIPT_DIR/javafx-common.sh"
FX_LIB=$(resolve_javafx_lib "$REPO_ROOT")

if [ ! -f "$OUT_DIR/gui/GameApp.class" ]; then
    echo "No hay clases compiladas en $OUT_DIR." >&2
    echo "Ejecuta primero: sh scripts/compile-gui.sh" >&2
    exit 1
fi

echo "Abriendo NetworkBuilder (JavaFX) ..."
exec java --module-path "$FX_LIB" --add-modules "$JAVAFX_MODULES" \
     -cp "$(to_native "$OUT_DIR")" gui.GameApp
