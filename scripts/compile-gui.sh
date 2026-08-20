#!/usr/bin/env sh
# compile-gui.sh - gemelo POSIX de compile-gui.ps1.
#
# Compila el nucleo (src/) MAS el bonus grafico (src-gui/) en out-gui/.
# No reemplaza la compilacion calificada: el nucleo de consola se sigue
# compilando aparte con `javac` puro hacia out/, sin ninguna referencia a JavaFX.
#
# Uso: sh scripts/compile-gui.sh

set -eu

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
REPO_ROOT=$(dirname "$SCRIPT_DIR")
OUT_DIR="$REPO_ROOT/out-gui"

# shellcheck source=./javafx-common.sh
. "$SCRIPT_DIR/javafx-common.sh"
FX_LIB=$(resolve_javafx_lib "$REPO_ROOT")

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

SOURCES=$(mktemp)
trap 'rm -f "$SOURCES"' EXIT
# Cada ruta va entrecomillada: el argfile de javac separa argumentos por
# espacios y varias carpetas del curso los contienen ("Algoritmica II").
find "$REPO_ROOT/src" "$REPO_ROOT/src-gui" -name '*.java' | while IFS= read -r f; do
    printf '"%s"\n' "$(to_native "$f")"
done > "$SOURCES"

echo "Compilando $(wc -l < "$SOURCES" | tr -d ' ') archivos .java en $OUT_DIR ..."
echo "  module-path: $FX_LIB"

javac -d "$(to_native "$OUT_DIR")" --module-path "$FX_LIB" \
      --add-modules "$JAVAFX_MODULES" "@$(to_native "$SOURCES")"

echo "OK: compilacion exitosa -> $OUT_DIR"
echo "Siguiente paso: sh scripts/run-gui.sh"
