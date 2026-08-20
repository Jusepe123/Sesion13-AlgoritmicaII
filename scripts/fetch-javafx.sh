#!/usr/bin/env sh
# fetch-javafx.sh - gemelo POSIX de fetch-javafx.ps1 (Git Bash / Linux / macOS).
#
# Descarga el SDK de JavaFX 21.x dentro de lib/. Solo afecta al bonus grafico
# (Tarea 5): el nucleo de consola se compila con `javac` puro y no depende de
# JavaFX. Si esto falla, el entregable calificado sigue funcionando.
#
# Uso:
#   sh scripts/fetch-javafx.sh
#   sh scripts/fetch-javafx.sh 21.0.5

set -u

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
REPO_ROOT=$(dirname "$SCRIPT_DIR")
LIB_DIR="$REPO_ROOT/lib"

# Plataforma del zip de Gluon.
case "$(uname -s)" in
    Linux*)                        PLATFORM="linux-x64" ;;
    Darwin*)                       PLATFORM=$([ "$(uname -m)" = "arm64" ] && echo "osx-aarch64" || echo "osx-x64") ;;
    MINGW*|MSYS*|CYGWIN*|Windows*) PLATFORM="windows-x64" ;;
    *)                             PLATFORM="windows-x64" ;;
esac

DEFAULT_CANDIDATES="21.0.9 21.0.8 21.0.7 21.0.6 21.0.5"
# Las instrucciones manuales siempre recomiendan la version fijada por defecto,
# aunque el intento fallido haya sido con un argumento mal escrito.
PREFERRED=$(echo "$DEFAULT_CANDIDATES" | awk '{print $1}')

if [ "$#" -ge 1 ]; then
    CANDIDATES="$1"
else
    CANDIDATES="$DEFAULT_CANDIDATES"
fi

installed_sdk() {
    [ -d "$LIB_DIR" ] || return 1
    for d in "$LIB_DIR"/javafx-sdk-*; do
        if [ -f "$d/lib/javafx.controls.jar" ]; then
            echo "$d"
            return 0
        fi
    done
    return 1
}

manual_instructions() {
    reason="$1"
    url="https://download2.gluonhq.com/openjfx/$PREFERRED/openjfx-${PREFERRED}_${PLATFORM}_bin-sdk.zip"
    cat <<EOF

======================================================================
 No se pudo obtener el SDK de JavaFX automaticamente.
 Motivo: $reason
======================================================================

 Pasos manuales (5 minutos):

   1. Descarga el SDK de JavaFX 21 para tu plataforma ($PLATFORM) desde:
        https://gluonhq.com/products/javafx/
      (elige: JavaFX $PREFERRED / tu SO / x64 / SDK)
      Enlace directo:
        $url

   2. Descomprime el zip. Adentro trae una carpeta 'javafx-sdk-$PREFERRED'.

   3. Copia esa carpeta a:
        $LIB_DIR

   4. Verifica que exista exactamente este archivo:
        $LIB_DIR/javafx-sdk-$PREFERRED/lib/javafx.controls.jar

   5. Luego ejecuta:
        sh scripts/compile-gui.sh
        sh scripts/run-gui.sh

 Recordatorio: el juego de consola NO necesita JavaFX.
 Sigue funcionando con scripts/compile.sh y scripts/run-console.sh.

EOF
}

download() {
    url="$1"
    out="$2"
    if command -v curl >/dev/null 2>&1; then
        curl -fL --retry 2 --connect-timeout 30 -o "$out" "$url"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "$out" "$url"
    else
        echo "  Ni curl ni wget estan disponibles." >&2
        return 127
    fi
}

extract() {
    zip="$1"
    dest="$2"
    if command -v unzip >/dev/null 2>&1; then
        unzip -q -o "$zip" -d "$dest"
    elif command -v powershell >/dev/null 2>&1; then
        powershell -NoProfile -Command "Expand-Archive -Path '$zip' -DestinationPath '$dest' -Force"
    else
        echo "  Ni unzip ni powershell estan disponibles para descomprimir." >&2
        return 127
    fi
}

if SDK=$(installed_sdk); then
    echo "JavaFX ya esta instalado: $SDK"
    exit 0
fi

mkdir -p "$LIB_DIR"

LAST_ERROR="desconocido"
for v in $CANDIDATES; do
    URL="https://download2.gluonhq.com/openjfx/$v/openjfx-${v}_${PLATFORM}_bin-sdk.zip"
    ZIP="$LIB_DIR/openjfx-${v}_${PLATFORM}_bin-sdk.zip"

    echo "Descargando JavaFX $v ..."
    echo "  $URL"
    if ! download "$URL" "$ZIP"; then
        LAST_ERROR="fallo la descarga de $v (red bloqueada o version inexistente)"
        echo "  $LAST_ERROR"
        rm -f "$ZIP"
        continue
    fi

    echo "Descomprimiendo en $LIB_DIR ..."
    if ! extract "$ZIP" "$LIB_DIR"; then
        LAST_ERROR="fallo la descompresion de $v"
        echo "  $LAST_ERROR"
        rm -f "$ZIP"
        continue
    fi
    rm -f "$ZIP"

    # Se verifica la carpeta de ESTA version, no "la ultima que haya".
    SDK_DIR="$LIB_DIR/javafx-sdk-$v"
    if [ -f "$SDK_DIR/lib/javafx.controls.jar" ]; then
        echo ""
        echo "OK: JavaFX $v instalado."
        echo "  SDK : $SDK_DIR"
        echo "  Jar : $SDK_DIR/lib/javafx.controls.jar"
        echo ""
        echo "Siguiente paso: sh scripts/compile-gui.sh"
        exit 0
    fi

    LAST_ERROR="el zip se descomprimio pero no aparecio $SDK_DIR/lib/javafx.controls.jar"
    echo "  Aviso: $LAST_ERROR"
done

manual_instructions "$LAST_ERROR"
exit 1
