#!/usr/bin/env sh
# javafx-common.sh - utilidades compartidas por compile-gui.sh y run-gui.sh.
#
# No se ejecuta directamente: se carga con `. scripts/javafx-common.sh`.

# javafx.animation NO es un modulo: Timeline/KeyFrame viven dentro de
# javafx.graphics. Pedirlo en --add-modules aborta con "module not found".
JAVAFX_MODULES="javafx.controls,javafx.graphics"

# En Git Bash el JDK es un binario de Windows y no entiende rutas '/c/...'.
# cygpath -m las convierte a 'C:/...' (con barras normales, seguras dentro del
# argfile de javac, donde '\' seria un caracter de escape). Fuera de Windows es
# la identidad.
to_native() {
    if command -v cygpath >/dev/null 2>&1; then
        cygpath -m "$1"
    else
        echo "$1"
    fi
}

resolve_javafx_lib() {
    repo_root="$1"
    lib_dir="$repo_root/lib"
    found=""
    for d in "$lib_dir"/javafx-sdk-*; do
        if [ -f "$d/lib/javafx.controls.jar" ]; then
            found=$(to_native "$d/lib")
        fi
    done

    if [ -z "$found" ]; then
        cat >&2 <<EOF

No se encontro el SDK de JavaFX en $lib_dir.
Ejecuta primero:
    sh scripts/fetch-javafx.sh
(ese script imprime tambien los pasos manuales si la descarga falla).

El juego de consola no necesita JavaFX: usa scripts/compile.sh.

EOF
        exit 1
    fi
    echo "$found"
}
