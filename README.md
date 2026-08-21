# NetworkBuilder

Juego de consola en Java 21 que implementa el algoritmo de Kruskal (Union-Find
con union por rango y compresion de caminos) como un juego de aceptacion de
aristas: el jugador construye una red de costo minimo eligiendo aristas una a
una, y el motor evalua el resultado contra el MST real calculado por Kruskal.

Trabajo de la materia Algoritmica II (UPB), Sesion 13. Proyecto en 6 tareas:
motor (DSU/Kruskal/juego), pruebas automatizadas, aplicacion de consola
interactiva, bonus grafico opcional (JavaFX) y esta documentacion/evidencia
final.

## Version de Java

Compilado y probado con:

```
openjdk version "21.0.8" 2025-07-15 LTS
OpenJDK Runtime Environment Temurin-21.0.8+9 (build 21.0.8+9-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.8+9 (build 21.0.8+9-LTS, mixed mode, sharing)
```

Cualquier JDK 21 (LTS) deberia funcionar igual; el codigo no usa preview
features.

## Compilar y ejecutar (nucleo de consola, sin JavaFX)

El nucleo calificado (`src/`) se compila con `javac` puro: no toca `lib/`, no
usa `--module-path` ni `--add-modules`. El bonus grafico (`src-gui/`) es
completamente independiente (ver seccion "Extension JavaFX" abajo).

### Windows (PowerShell)

```powershell
# 1. Compilar src/ -> out/
powershell -ExecutionPolicy Bypass -File scripts\compile.ps1

# 2. Correr el juego interactivo
powershell -ExecutionPolicy Bypass -File scripts\run-console.ps1

# 3. Correr la bateria de pruebas/evidencia (10 pruebas calificadas + 1 traza DSU)
powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1
```

Para reproducir una partida grabada (entrada redirigida, sin retipear nada):

```powershell
cmd /c "powershell -ExecutionPolicy Bypass -File scripts\run-console.ps1 < scripts\inputs\input-modo1-nivel1.txt"
```

(Se recomienda ese `cmd /c ... < archivo` en vez de
`Get-Content archivo | ... run-console.ps1`: PowerShell 5.1 antepone un BOM al
convertir texto para un pipe hacia un ejecutable nativo, lo cual corrompe la
primera linea de entrada de `java`. La redireccion `<` no tiene ese problema.)

### Linux / macOS / Git Bash

```sh
# 1. Compilar src/ -> out/
sh scripts/compile.sh

# 2. Correr el juego interactivo
sh scripts/run-console.sh

# 3. Correr la bateria de pruebas/evidencia
sh scripts/run-tests.sh

# Reproducir una partida grabada:
sh scripts/run-console.sh < scripts/inputs/input-modo1-nivel1.txt
```

Todos los comandos de esta seccion fueron ejecutados de verdad (no solo
escritos) como parte de la Tarea 6; ver `evidencia.md` para las salidas
capturadas.

## Modos disponibles

1. **Entrenamiento** — muestra el estado completo (aristas, costo acumulado,
   componentes restantes) en cada turno y explica por que cada arista fue
   aceptada o rechazada.
2. **Desafio** — misma mecanica, pero oculta la informacion de componentes
   antes de elegir; solo informa aceptada/rechazada y el costo acumulado.
3. **Entrenador de Kruskal** — presenta todas las aristas en orden ascendente
   de peso y pide adivinar si Kruskal la aceptaria o rechazaria antes de
   revelar la respuesta real.

## Niveles

| Nivel | Nombre | n | Costo MST verificado | Notas |
|---|---|---|---|---|
| 1 | Tutorial | 5 | 11 | — |
| 2 | Desafio | 6 | 13 | — |
| 3 | Empates | 6 | 19 | 8 MST distintos validos (empates de peso) |
| 4 | Desconectado | 7 | 12 (bosque, no MST) | Dos componentes ({0,1,2,3} y {4,5,6}); no existe MST global |

## Clasificacion de puntaje (`game.ScoreCalculator.classify`)

| Puntaje | Clasificacion |
|---|---|
| 100 | Ingeniero optimo |
| 85-99 | Diseno excelente |
| 70-84 | Red funcional |
| 50-69 | Red costosa |
| 0-49 | Requiere revision |

## Estructura del proyecto

Arbol real (verificado con `find`/`Get-ChildItem` en esta tarea; `out/`,
`out-gui/` y `lib/` estan en `.gitignore` porque son generados/descargados):

```
NetworkBuilder/
├── README.md
├── evidencia.md
├── prompts.md
├── .gitignore
├── src/                          # nucleo calificado (Tareas 1-4), javac puro
│   ├── model/                    # Edge, Graph
│   │   ├── Edge.java
│   │   └── Graph.java
│   ├── dsu/                      # Union-Find (union por rango + compresion de caminos)
│   │   └── DisjointSet.java      # copiado de Sesion12/Sesion12_DSU (Tarea 1)
│   ├── algorithm/                # Kruskal
│   │   ├── Kruskal.java
│   │   └── KruskalResult.java
│   ├── game/                     # motor del juego (unico lugar que orquesta Kruskal via el DSU)
│   │   ├── GameEvaluator.java
│   │   ├── GameState.java
│   │   ├── GameSummary.java
│   │   ├── Levels.java
│   │   ├── NetworkGame.java
│   │   ├── ScoreCalculator.java
│   │   └── SelectionOutcome.java
│   ├── console/                  # UI de consola (3 modos)
│   │   ├── ChallengeMode.java
│   │   ├── ConsoleUI.java
│   │   ├── InputReader.java
│   │   ├── KruskalTrainerMode.java
│   │   ├── ModeRunner.java
│   │   ├── ResultPrinter.java
│   │   └── TrainingMode.java
│   └── app/                      # puntos de entrada
│       ├── Main.java             # juego interactivo (Tarea 4)
│       └── TestRunner.java       # 10 pruebas calificadas + 1 traza DSU (Tarea 3)
├── src-gui/                      # bonus grafico opcional (Tarea 5), NO calificado
│   └── gui/
│       ├── ComparisonPanel.java
│       ├── GameApp.java
│       ├── GraphCanvas.java
│       └── KruskalAnimator.java
├── scripts/
│   ├── compile.ps1 / compile.sh              # javac puro, src/ -> out/ (Tarea 6)
│   ├── run-console.ps1 / run-console.sh      # java -cp out app.Main (Tarea 6)
│   ├── run-tests.ps1 / run-tests.sh          # java -cp out app.TestRunner (Tarea 6)
│   ├── fetch-javafx.ps1 / fetch-javafx.sh    # descarga el SDK de JavaFX en lib/ (Tarea 5)
│   ├── compile-gui.ps1 / compile-gui.sh      # src/+src-gui/ -> out-gui/, con JavaFX (Tarea 5)
│   ├── run-gui.ps1 / run-gui.sh              # abre la ventana del bonus (Tarea 5)
│   ├── javafx-common.ps1 / javafx-common.sh  # resuelve la ruta del SDK de JavaFX
│   └── inputs/                               # entradas grabadas para evidencia reproducible
│       ├── input-modo1-nivel1.txt
│       ├── input-modo2-nivel2.txt
│       ├── input-modo3-nivel1.txt
│       ├── input-modo1-nivel3.txt
│       └── input-modo1-nivel4.txt
├── out/                          # generado por compile.ps1/.sh (gitignored)
├── out-gui/                      # generado por compile-gui.ps1/.sh (gitignored)
└── lib/                          # SDK de JavaFX descargado por fetch-javafx (gitignored)
    └── javafx-sdk-21.0.9/
```

## Extension JavaFX (bonus)

El bonus grafico es completamente opcional: no afecta la evaluacion del
nucleo de consola y, si no se instala, el resto del proyecto sigue
funcionando igual.

### Uso automatico

```powershell
# 1. Descargar el SDK de JavaFX 21.x (Windows x64) en lib/
powershell -ExecutionPolicy Bypass -File scripts\fetch-javafx.ps1

# 2. Compilar src/ + src-gui/ -> out-gui/ (con --module-path hacia lib/)
powershell -ExecutionPolicy Bypass -File scripts\compile-gui.ps1

# 3. Abrir la ventana
powershell -ExecutionPolicy Bypass -File scripts\run-gui.ps1
```

Equivalente POSIX: `sh scripts/fetch-javafx.sh`, `sh scripts/compile-gui.sh`,
`sh scripts/run-gui.sh`.

`fetch-javafx` prueba, de la version mas nueva a la mas vieja
(21.0.9 → 21.0.5), descargar
`https://download2.gluonhq.com/openjfx/<version>/openjfx-<version>_windows-x64_bin-sdk.zip`
y descomprimirlo en `lib/`. Si ya existe un SDK valido en `lib/` (con
`lib\javafx.controls.jar` presente), no vuelve a descargarlo salvo que se
pase `-Force`.

### Fallback manual (si la descarga automatica falla, p. ej. sin acceso a internet)

1. Descargar el SDK de JavaFX 21 para Windows x64 desde
   `https://gluonhq.com/products/javafx/` (elegir JavaFX 21.0.9 / Windows /
   x64 / SDK), o directamente:
   `https://download2.gluonhq.com/openjfx/21.0.9/openjfx-21.0.9_windows-x64_bin-sdk.zip`
2. Descomprimir el zip. Contiene una carpeta `javafx-sdk-21.0.9`.
3. Copiar esa carpeta dentro de `lib/` (queda `lib\javafx-sdk-21.0.9\`).
4. Verificar que exista `lib\javafx-sdk-21.0.9\lib\javafx.controls.jar`.
5. Ejecutar `scripts\compile-gui.ps1` y luego `scripts\run-gui.ps1` como de
   costumbre.

## Demo web para la presentacion (QR)

Ademas de las interfaces de consola y JavaFX, el proyecto incluye una version
web estatica y adaptable a celulares en `web/`. Usa los mismos cuatro grafos y
la misma mecanica del juego: el jugador selecciona aristas y Union-Find evita
ciclos. Cada persona que abre el enlace juega una partida independiente; no
requiere instalar Java ni crear una cuenta.

### Ejecutarla en la red local

Desde la raiz del proyecto, se necesita Python 3:

```sh
python3 -m http.server 8080 --directory web
```

En la misma computadora se abre `http://localhost:8080`. Para que dispositivos
conectados al mismo Wi-Fi accedan directamente, se puede usar la IP local de la
computadora, por ejemplo `http://192.168.x.x:8080` (puede requerir permitir el
puerto 8080 en el firewall).

### Publicarla temporalmente con Cloudflare Tunnel

Esta opcion es conveniente para una clase porque evita configurar el router o
compartir una IP local. Mantener el servidor anterior abierto y, en otra
terminal, ejecutar:

```sh
# Descargar cloudflared una sola vez en Linux x64 (o instalarlo desde
# https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/downloads/)
curl -fL https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64 -o /tmp/cloudflared
chmod +x /tmp/cloudflared

# Crear un enlace publico temporal
/tmp/cloudflared tunnel --url http://127.0.0.1:8080
```

Cloudflare imprimira una URL similar a
`https://palabras-aleatorias.trycloudflare.com`. Abrirla primero desde el
telefono para comprobarla y mostrar esa URL como codigo QR. Los *quick tunnels*
no son permanentes: el enlace deja de funcionar cuando se cierra `cloudflared`,
se suspende la computadora o se pierde la conexion. Para una publicacion
permanente, usar un hosting estatico (por ejemplo GitHub Pages) en lugar del
tunel temporal.

### Crear el QR

Con la URL publica ya verificada, se puede generar un PNG de alta resolucion:

```sh
curl -fL 'https://api.qrserver.com/v1/create-qr-code/?size=900x900&format=png&data=PEGA_AQUI_LA_URL_PUBLICA' -o web/networkbuilder-qr.png
```

El archivo de QR se ignora intencionalmente en Git, porque cada URL de un quick
tunnel es distinta y temporal.

## Entregable final

Nombre del zip a entregar: `[TODO: confirmar apellido/nombre]`.
