#!/usr/bin/env sh
# compile.sh - gemelo POSIX de compile.ps1. Compila el nucleo calificado
# (src/) hacia out/ con javac puro. Cero JavaFX en el classpath.
#
# Uso: sh scripts/compile.sh
#
# Nota: en Git Bash sobre Windows, javac.exe es un binario nativo; se le pasan
# los archivos fuente como argumentos directos de la linea de comandos (no via
# @argfile) para que Git Bash traduzca las rutas POSIX a rutas de Windows
# automaticamente.

set -u

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
REPO_ROOT=$(dirname "$SCRIPT_DIR")
SRC_DIR="$REPO_ROOT/src"
OUT_DIR="$REPO_ROOT/out"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

set --
while IFS= read -r f; do
    set -- "$@" "$f"
done <<EOF
$(find "$SRC_DIR" -name '*.java')
EOF

echo "Compilando $# archivos .java (src/) en $OUT_DIR ..."

javac -d "$OUT_DIR" "$@"
CODE=$?

if [ "$CODE" -ne 0 ]; then
    echo "Fallo la compilacion (exit $CODE)." >&2
    exit "$CODE"
fi

echo "OK: compilacion exitosa -> $OUT_DIR"
echo "Siguiente paso: sh scripts/run-console.sh"
