# Evidencia — NetworkBuilder

Todo el contenido de este documento (salvo las secciones marcadas `[TODO: ...]`,
que el estudiante debe completar personalmente) proviene de comandos
realmente ejecutados sobre el codigo final del proyecto.

**Entorno de captura:** Windows 11, `openjdk version "21.0.8"` (Temurin
21.0.8+9), PowerShell 5.1. Fecha de captura: 2026-08-20.

**Reproducibilidad:** cualquier bloque de esta seccion se puede regenerar
ejecutando (Windows, desde `cmd.exe` o con el wrapper `cmd /c` que se muestra
abajo — ver la nota en README.md: una PowerShell interactiva rechaza el `<`
suelto con "the '<' operator is reserved for future use", y ademas
PowerShell 5.1 antepone un caracter espurio al convertir texto para un pipe
hacia un ejecutable nativo, lo que corrompe la primera linea de entrada):

```
powershell -ExecutionPolicy Bypass -File scripts\compile.ps1
powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1
cmd /c "powershell -ExecutionPolicy Bypass -File scripts\run-console.ps1 < scripts\inputs\<archivo>.txt"
```

(en Git Bash / Linux / macOS, donde `<` funciona de forma directa:
`sh scripts/compile.sh`, `sh scripts/run-tests.sh`,
`sh scripts/run-console.sh < scripts/inputs/<archivo>.txt`).

---

## 1. Evidencia de consola por nivel

### Nivel 1 — Tutorial (n=5, MST verificado costo 11)

**Modo 1 (Entrenamiento), partida completa hasta MST optimo.**
Script: `scripts\run-console.ps1` — Entrada: `scripts/inputs/input-modo1-nivel1.txt`.

```
====================
  NetworkBuilder
====================
Construye una red de costo minimo eligiendo aristas, una a una.

Elige un nivel (1-4):
Modos disponibles:
  1) Entrenamiento (muestra componentes y explicacion completa)
  2) Desafio (oculta componentes)
  3) Entrenador de Kruskal (adivina aceptar/rechazar por arista)
Elige un modo (1-3):
=========================
  Modo 1: Entrenamiento
=========================

--- Estado actual ---
Vertices: 0..4
Aristas:
   1) 0 -- 1 (costo 4)
   2) 0 -- 2 (costo 2)
   3) 1 -- 2 (costo 1)
   4) 3 -- 4 (costo 3)
   5) 1 -- 3 (costo 7)
   6) 2 -- 3 (costo 5)
   7) 2 -- 4 (costo 8)
Costo acumulado: 0
Componentes restantes: 5

Elige una arista (indice, o "u v"):
Seleccion: 1 -- 2 (costo 1)

ACEPTADA
1 y 2 estaban en componentes distintos.

Costo acumulado: 1
Componentes restantes: 4

--- Estado actual ---
Vertices: 0..4
Aristas:
   1) 0 -- 1 (costo 4)
   2) 0 -- 2 (costo 2)
   3) 1 -- 2 (costo 1)
   4) 3 -- 4 (costo 3)
   5) 1 -- 3 (costo 7)
   6) 2 -- 3 (costo 5)
   7) 2 -- 4 (costo 8)
Costo acumulado: 1
Componentes restantes: 4

Elige una arista (indice, o "u v"):
Seleccion: 0 -- 2 (costo 2)

ACEPTADA
0 y 2 estaban en componentes distintos.

Costo acumulado: 3
Componentes restantes: 3

--- Estado actual ---
Vertices: 0..4
Aristas:
   1) 0 -- 1 (costo 4)
   2) 0 -- 2 (costo 2)
   3) 1 -- 2 (costo 1)
   4) 3 -- 4 (costo 3)
   5) 1 -- 3 (costo 7)
   6) 2 -- 3 (costo 5)
   7) 2 -- 4 (costo 8)
Costo acumulado: 3
Componentes restantes: 3

Elige una arista (indice, o "u v"):
Seleccion: 3 -- 4 (costo 3)

ACEPTADA
3 y 4 estaban en componentes distintos.

Costo acumulado: 6
Componentes restantes: 2

--- Estado actual ---
Vertices: 0..4
Aristas:
   1) 0 -- 1 (costo 4)
   2) 0 -- 2 (costo 2)
   3) 1 -- 2 (costo 1)
   4) 3 -- 4 (costo 3)
   5) 1 -- 3 (costo 7)
   6) 2 -- 3 (costo 5)
   7) 2 -- 4 (costo 8)
Costo acumulado: 6
Componentes restantes: 2

Elige una arista (indice, o "u v"):
Seleccion: 2 -- 3 (costo 5)

ACEPTADA
2 y 3 estaban en componentes distintos.

Costo acumulado: 11
Componentes restantes: 1

Partida finalizada.

=== RESULTADO ===

Costo del jugador: 11
Costo optimo MST: 11

EXCELENTE:
La red construida es un MST.

Puntaje: 100
Clasificacion: Ingeniero optimo

Jugar de nuevo? (s/n):
Gracias por jugar NetworkBuilder.
```

**Modo 3 (Entrenador de Kruskal), las 7 aristas del nivel evaluadas en orden de peso.**
Script: `scripts\run-console.ps1` — Entrada: `scripts/inputs/input-modo3-nivel1.txt`.

```
====================
  NetworkBuilder
====================
Construye una red de costo minimo eligiendo aristas, una a una.

Elige un nivel (1-4):
Modos disponibles:
  1) Entrenamiento (muestra componentes y explicacion completa)
  2) Desafio (oculta componentes)
  3) Entrenador de Kruskal (adivina aceptar/rechazar por arista)
Elige un modo (1-3):
=================================
  Modo 3: Entrenador de Kruskal
=================================
Para cada arista, en orden ascendente de peso, adivina si Kruskal la
aceptaria (A) o la rechazaria (R). Luego se revela la respuesta real.

Arista 1/7: 1 -- 2 (costo 1)
A) Aceptar / R) Rechazar:   Extremos 1 y 2 estaban en componentes distintos.
  Decision correcta: ACEPTAR (Kruskal la acepta)
  Tu respuesta: ACEPTAR
  Correcto!
  Costo acumulado: 1 | Componentes restantes: 4

Arista 2/7: 0 -- 2 (costo 2)
A) Aceptar / R) Rechazar:   Extremos 0 y 2 estaban en componentes distintos.
  Decision correcta: ACEPTAR (Kruskal la acepta)
  Tu respuesta: ACEPTAR
  Correcto!
  Costo acumulado: 3 | Componentes restantes: 3

Arista 3/7: 3 -- 4 (costo 3)
A) Aceptar / R) Rechazar:   Extremos 3 y 4 estaban en componentes distintos.
  Decision correcta: ACEPTAR (Kruskal la acepta)
  Tu respuesta: ACEPTAR
  Correcto!
  Costo acumulado: 6 | Componentes restantes: 2

Arista 4/7: 0 -- 1 (costo 4)
A) Aceptar / R) Rechazar:   Extremos 0 y 1 ya estaban en el mismo componente.
  Decision correcta: RECHAZAR (Kruskal la rechaza)
  Tu respuesta: RECHAZAR
  Correcto!
  Costo acumulado: 6 | Componentes restantes: 2

Arista 5/7: 2 -- 3 (costo 5)
A) Aceptar / R) Rechazar:   Extremos 2 y 3 estaban en componentes distintos.
  Decision correcta: ACEPTAR (Kruskal la acepta)
  Tu respuesta: ACEPTAR
  Correcto!
  Costo acumulado: 11 | Componentes restantes: 1

Arista 6/7: 1 -- 3 (costo 7)
A) Aceptar / R) Rechazar:   Extremos 1 y 3 ya estaban en el mismo componente.
  Decision correcta: RECHAZAR (Kruskal la rechaza)
  Tu respuesta: RECHAZAR
  Correcto!
  Costo acumulado: 11 | Componentes restantes: 1

Arista 7/7: 2 -- 4 (costo 8)
A) Aceptar / R) Rechazar:   Extremos 2 y 4 ya estaban en el mismo componente.
  Decision correcta: RECHAZAR (Kruskal la rechaza)
  Tu respuesta: RECHAZAR
  Correcto!
  Costo acumulado: 11 | Componentes restantes: 1

--- Resumen del entrenador ---
Aristas evaluadas: 7
Predicciones correctas: 7
Predicciones incorrectas: 0
Intentos de ciclo reales (game.cycleAttempts()): 3

=== RESULTADO ===

Costo del jugador: 11
Costo optimo MST: 11

EXCELENTE:
La red construida es un MST.

Puntaje: 70
Clasificacion: Red funcional

Jugar de nuevo? (s/n):
Gracias por jugar NetworkBuilder.
```

Nota: en Modo 3 la puntuacion (`ScoreCalculator`) penaliza los 3 intentos de
ciclo reales generados al recorrer TODAS las aristas del grafo (incluidas las
que Kruskal rechaza), aun cuando las 7 predicciones del jugador fueron
correctas: por eso el puntaje (70, "Red funcional") es menor que en el Modo 1
(100, "Ingeniero optimo") pese a llegar al mismo costo optimo (11).

### Nivel 2 — Desafio (n=6, MST verificado costo 13)

**Modo 2 (Desafio), partida completa hasta MST optimo.**
Script: `scripts\run-console.ps1` — Entrada: `scripts/inputs/input-modo2-nivel2.txt`.

```
====================
  NetworkBuilder
====================
Construye una red de costo minimo eligiendo aristas, una a una.

Elige un nivel (1-4):
Modos disponibles:
  1) Entrenamiento (muestra componentes y explicacion completa)
  2) Desafio (oculta componentes)
  3) Entrenador de Kruskal (adivina aceptar/rechazar por arista)
Elige un modo (1-3):
====================
  Modo 2: Desafio
====================

--- Estado actual ---
Vertices: 0..5
Aristas:
   1) 0 -- 1 (costo 3)
   2) 0 -- 2 (costo 6)
   3) 1 -- 2 (costo 2)
   4) 1 -- 3 (costo 5)
   5) 2 -- 3 (costo 3)
   6) 2 -- 4 (costo 5)
   7) 3 -- 4 (costo 4)
   8) 3 -- 5 (costo 7)
   9) 4 -- 5 (costo 1)
  10) 1 -- 5 (costo 9)
Costo acumulado: 0

Elige una arista (indice, o "u v"):
Seleccion: 4 -- 5 (costo 1)

ACEPTADA

Costo acumulado: 1
Componentes restantes: 5

--- Estado actual ---
Vertices: 0..5
Aristas:
   1) 0 -- 1 (costo 3)
   2) 0 -- 2 (costo 6)
   3) 1 -- 2 (costo 2)
   4) 1 -- 3 (costo 5)
   5) 2 -- 3 (costo 3)
   6) 2 -- 4 (costo 5)
   7) 3 -- 4 (costo 4)
   8) 3 -- 5 (costo 7)
   9) 4 -- 5 (costo 1)
  10) 1 -- 5 (costo 9)
Costo acumulado: 1

Elige una arista (indice, o "u v"):
Seleccion: 1 -- 2 (costo 2)

ACEPTADA

Costo acumulado: 3
Componentes restantes: 4

--- Estado actual ---
Vertices: 0..5
Aristas:
   1) 0 -- 1 (costo 3)
   2) 0 -- 2 (costo 6)
   3) 1 -- 2 (costo 2)
   4) 1 -- 3 (costo 5)
   5) 2 -- 3 (costo 3)
   6) 2 -- 4 (costo 5)
   7) 3 -- 4 (costo 4)
   8) 3 -- 5 (costo 7)
   9) 4 -- 5 (costo 1)
  10) 1 -- 5 (costo 9)
Costo acumulado: 3

Elige una arista (indice, o "u v"):
Seleccion: 0 -- 1 (costo 3)

ACEPTADA

Costo acumulado: 6
Componentes restantes: 3

--- Estado actual ---
Vertices: 0..5
Aristas:
   1) 0 -- 1 (costo 3)
   2) 0 -- 2 (costo 6)
   3) 1 -- 2 (costo 2)
   4) 1 -- 3 (costo 5)
   5) 2 -- 3 (costo 3)
   6) 2 -- 4 (costo 5)
   7) 3 -- 4 (costo 4)
   8) 3 -- 5 (costo 7)
   9) 4 -- 5 (costo 1)
  10) 1 -- 5 (costo 9)
Costo acumulado: 6

Elige una arista (indice, o "u v"):
Seleccion: 2 -- 3 (costo 3)

ACEPTADA

Costo acumulado: 9
Componentes restantes: 2

--- Estado actual ---
Vertices: 0..5
Aristas:
   1) 0 -- 1 (costo 3)
   2) 0 -- 2 (costo 6)
   3) 1 -- 2 (costo 2)
   4) 1 -- 3 (costo 5)
   5) 2 -- 3 (costo 3)
   6) 2 -- 4 (costo 5)
   7) 3 -- 4 (costo 4)
   8) 3 -- 5 (costo 7)
   9) 4 -- 5 (costo 1)
  10) 1 -- 5 (costo 9)
Costo acumulado: 9

Elige una arista (indice, o "u v"):
Seleccion: 3 -- 4 (costo 4)

ACEPTADA

Costo acumulado: 13
Componentes restantes: 1

Partida finalizada.

=== RESULTADO ===

Costo del jugador: 13
Costo optimo MST: 13

EXCELENTE:
La red construida es un MST.

Puntaje: 100
Clasificacion: Ingeniero optimo

Jugar de nuevo? (s/n):
Gracias por jugar NetworkBuilder.
```

### Nivel 3 — Empates (n=6, MST verificado costo 19, 8 MST distintos)

**Modo 1 (Entrenamiento), una de las combinaciones optimas posibles (empates).**
Script: `scripts\run-console.ps1` — Entrada: `scripts/inputs/input-modo1-nivel3.txt`.

```
====================
  NetworkBuilder
====================
Construye una red de costo minimo eligiendo aristas, una a una.

Elige un nivel (1-4):
Modos disponibles:
  1) Entrenamiento (muestra componentes y explicacion completa)
  2) Desafio (oculta componentes)
  3) Entrenador de Kruskal (adivina aceptar/rechazar por arista)
Elige un modo (1-3):
=========================
  Modo 1: Entrenamiento
=========================

--- Estado actual ---
Vertices: 0..5
Aristas:
   1) 0 -- 1 (costo 2)
   2) 0 -- 2 (costo 2)
   3) 1 -- 2 (costo 3)
   4) 1 -- 3 (costo 4)
   5) 2 -- 3 (costo 4)
   6) 2 -- 4 (costo 5)
   7) 3 -- 4 (costo 5)
   8) 3 -- 5 (costo 6)
   9) 4 -- 5 (costo 6)
  10) 2 -- 5 (costo 7)
Costo acumulado: 0
Componentes restantes: 6

Elige una arista (indice, o "u v"):
Seleccion: 0 -- 1 (costo 2)

ACEPTADA
0 y 1 estaban en componentes distintos.

Costo acumulado: 2
Componentes restantes: 5

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 2
Componentes restantes: 5

Elige una arista (indice, o "u v"):
Seleccion: 0 -- 2 (costo 2)

ACEPTADA
0 y 2 estaban en componentes distintos.

Costo acumulado: 4
Componentes restantes: 4

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 4
Componentes restantes: 4

Elige una arista (indice, o "u v"):
Seleccion: 1 -- 3 (costo 4)

ACEPTADA
1 y 3 estaban en componentes distintos.

Costo acumulado: 8
Componentes restantes: 3

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 8
Componentes restantes: 3

Elige una arista (indice, o "u v"):
Seleccion: 2 -- 4 (costo 5)

ACEPTADA
2 y 4 estaban en componentes distintos.

Costo acumulado: 13
Componentes restantes: 2

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 13
Componentes restantes: 2

Elige una arista (indice, o "u v"):
Seleccion: 3 -- 5 (costo 6)

ACEPTADA
3 y 5 estaban en componentes distintos.

Costo acumulado: 19
Componentes restantes: 1

Partida finalizada.

=== RESULTADO ===

Costo del jugador: 19
Costo optimo MST: 19

EXCELENTE:
La red construida es un MST.

Puntaje: 100
Clasificacion: Ingeniero optimo

Jugar de nuevo? (s/n):
Gracias por jugar NetworkBuilder.
```

(La tabla de aristas es identica en cada turno: solo cambian "Costo acumulado"
y "Componentes restantes"; se omitio la repeticion literal de la tabla para
abreviar el bloque, sin alterar ninguna linea de resultado real. La captura
completa, sin abreviar, se reproduce en cualquier momento con
`scripts\run-console.ps1 < scripts\inputs\input-modo1-nivel3.txt`.)

Esta seleccion (aristas (0,1), (0,2), (1,3), (2,4), (3,5)) es uno de los 8 MST
validos del Nivel 3: `app.TestRunner` (Prueba 4, Seccion 2) confirma con una
selección *distinta* de aristas — (0,1), (0,2), (2,3), (3,4), (4,5) — que
tambien suma 19 y tambien se clasifica `OPTIMAL`, evidenciando el empate.

### Nivel 4 — Desconectado (n=7, dos componentes, sin MST global)

**Modo 1 (Entrenamiento), las 7 aristas evaluadas (5 aceptadas, 2 rechazadas por ciclo).**
Script: `scripts\run-console.ps1` — Entrada: `scripts/inputs/input-modo1-nivel4.txt`.

```
====================
  NetworkBuilder
====================
Construye una red de costo minimo eligiendo aristas, una a una.

Elige un nivel (1-4):
Modos disponibles:
  1) Entrenamiento (muestra componentes y explicacion completa)
  2) Desafio (oculta componentes)
  3) Entrenador de Kruskal (adivina aceptar/rechazar por arista)
Elige un modo (1-3):
=========================
  Modo 1: Entrenamiento
=========================

--- Estado actual ---
Vertices: 0..6
Aristas:
   1) 0 -- 1 (costo 2)
   2) 1 -- 2 (costo 3)
   3) 2 -- 3 (costo 1)
   4) 0 -- 3 (costo 6)
   5) 4 -- 5 (costo 2)
   6) 5 -- 6 (costo 4)
   7) 4 -- 6 (costo 5)
Costo acumulado: 0
Componentes restantes: 7

Elige una arista (indice, o "u v"):
Seleccion: 0 -- 1 (costo 2)

ACEPTADA
0 y 1 estaban en componentes distintos.

Costo acumulado: 2
Componentes restantes: 6

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 2
Componentes restantes: 6

Elige una arista (indice, o "u v"):
Seleccion: 1 -- 2 (costo 3)

ACEPTADA
1 y 2 estaban en componentes distintos.

Costo acumulado: 5
Componentes restantes: 5

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 5
Componentes restantes: 5

Elige una arista (indice, o "u v"):
Seleccion: 2 -- 3 (costo 1)

ACEPTADA
2 y 3 estaban en componentes distintos.

Costo acumulado: 6
Componentes restantes: 4

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 6
Componentes restantes: 4

Elige una arista (indice, o "u v"):
Seleccion: 0 -- 3 (costo 6)

RECHAZADA
0 y 3 ya pertenecen al mismo componente.
La arista cerraria un ciclo.

Costo acumulado: 6
Componentes restantes: 4

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 6
Componentes restantes: 4

Elige una arista (indice, o "u v"):
Seleccion: 4 -- 5 (costo 2)

ACEPTADA
4 y 5 estaban en componentes distintos.

Costo acumulado: 8
Componentes restantes: 3

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 8
Componentes restantes: 3

Elige una arista (indice, o "u v"):
Seleccion: 5 -- 6 (costo 4)

ACEPTADA
5 y 6 estaban en componentes distintos.

Costo acumulado: 12
Componentes restantes: 2

--- Estado actual ---
[... tabla de aristas identica omitida por brevedad ...]
Costo acumulado: 12
Componentes restantes: 2

Elige una arista (indice, o "u v"):
Seleccion: 4 -- 6 (costo 5)

RECHAZADA
4 y 6 ya pertenecen al mismo componente.
La arista cerraria un ciclo.

Costo acumulado: 12
Componentes restantes: 2

Partida finalizada.

=== RESULTADO ===

No existe un MST global: el grafo es desconectado.

El grafo de origen no es conexo: ningun jugador puede completar
una red que abarque todos los vertices en un solo componente.
Aristas seleccionadas por el jugador: 5

Puntaje: N/A
Clasificacion: N/A

Jugar de nuevo? (s/n):
Gracias por jugar NetworkBuilder.
```

(Igual que en el Nivel 3, se omitio la repeticion literal de la tabla de
aristas identica entre turnos; ninguna linea de resultado real fue alterada.)

---

## 2. Tabla de pruebas

Fuente: salida real de `scripts\run-tests.ps1` (`app.TestRunner`), capturada
en esta misma tarea. Las 10 pruebas obligatorias (calificadas) son
`prueba1`..`prueba10`; la traza DSU (no calificada) se documenta aparte en la
Seccion 3.

| Prueba | Entrada | Resultado esperado | Resultado obtenido | Veredicto |
|---|---|---|---|---|
| 1. Solucion optima (Nivel 2) | 5 aristas del MST verificado: (4,5),(1,2),(0,1),(2,3),(3,4) | currentCost=13, isConnected=true, outcome=OPTIMAL | currentCost()=13, isConnected()=true, outcome=OPTIMAL | PASA |
| 2. Solucion factible no optima (Nivel 2) | Arbol de expansion alterno: (0,1),(1,2),(2,3),(3,5),(4,5) (suma 16) | isConnected=true, playerCost=16, mstCost=13, outcome=FEASIBLE_NOT_OPTIMAL | isConnected()=true, playerCost()=16, mstCost()=13, outcome()=FEASIBLE_NOT_OPTIMAL | PASA |
| 3. Intento de ciclo (Nivel 1) | Aceptar (0,2) y (1,2); luego intentar (0,1) | (0,1) rechazada, alreadyConnected=true, cycleAttempts=1 | accepted=false, alreadyConnected=true, rootU=0, rootV=0, cycleAttempts()=1 | PASA |
| 4. Empates (Nivel 3, dos MST distintos) | Seleccion A: (0,1),(0,2),(1,3),(2,4),(3,5) — Seleccion B: (0,1),(0,2),(2,3),(3,4),(4,5) | costA=19 OPTIMAL, costB=19 OPTIMAL, conjuntos de aristas distintos | costA=19 OPTIMAL, costB=19 OPTIMAL, distintos=true | PASA |
| 5. Grafo desconectado (Nivel 4) | Todas las aristas del grafo, en orden de declaracion | 5 aceptadas, remainingComponents=2, Kruskal.mst().size()=5 (<n-1=6), outcome=IMPOSSIBLE | aceptadas=5, remainingComponents()=2, mst.size()=5, outcome()=IMPOSSIBLE | PASA |
| 6. Comparacion directa con Kruskal (Niveles 1 y 2) | `Kruskal.mst`/`totalWeight` sobre los grafos completos de Nivel 1 y Nivel 2 | totalWeight Nivel1=11, totalWeight Nivel2=13 | total1=11, total2=13 | PASA |
| 7. Grafo trivial de un solo nodo | `new Graph(1, List.of())` | isConnected=true de inmediato, outcome=OPTIMAL, playerCost=0 | connected=true, outcome()=OPTIMAL, playerCost()=0 | PASA |
| 8. Una sola arista | `new Graph(2, [(0,1,5)])`, seleccionar la unica arista | accepted=true, playerCost=mstCost=5, outcome=OPTIMAL | accepted=true, playerCost()=5, mstCost()=5, outcome()=OPTIMAL | PASA |
| 9. Pesos todos iguales (K4, peso 10) | K4 con 6 aristas de peso 10; se aceptan 3 en forma de estrella desde el vertice 0 | playerCost=30, outcome=OPTIMAL sin importar el trio elegido | playerCost()=30, outcome()=OPTIMAL | PASA |
| 10. Validacion de entradas | `new Graph(-1, [])`; `new Graph(3, [(0,5,1)])`; `new Edge(2,2,5)` | Las 3 llamadas lanzan IllegalArgumentException | 3 excepciones lanzadas con mensajes: "El grafo debe tener al menos 1 vertice (n >= 1)"; "La arista (0, 5) referencia un vertice fuera de rango [0, 3)"; "Una arista no puede ser un lazo (u == v): 2" | PASA |

**10/10 pruebas calificadas: PASA.**

---

## 3. Traza de DSU (>= 3 aristas)

Fuente: `pruebaTrazaDSU()` dentro de `app.TestRunner`, salida real capturada
por `scripts\run-tests.ps1` en esta tarea (no calificada; documenta la
evolucion del DSU dentro de `NetworkGame` sobre el Nivel 2, vía
`SelectionOutcome`, sin llamar directamente a `dsu.DisjointSet`).

```
=== Traza DSU (evidencia, Nivel 2) ===
  --- Paso 1: select(Edge[u=4, v=5, weight=1]) ---
    ANTES:  remainingComponents() = 6
    DESPUES: accepted=true, rootU=4, rootV=5, componentsAfter=5, costAfter=1
  --- Paso 2: select(Edge[u=1, v=2, weight=2]) ---
    ANTES:  remainingComponents() = 5
    DESPUES: accepted=true, rootU=1, rootV=2, componentsAfter=4, costAfter=3
  --- Paso 3: select(Edge[u=0, v=1, weight=3]) ---
    ANTES:  remainingComponents() = 4
    DESPUES: accepted=true, rootU=0, rootV=1, componentsAfter=3, costAfter=6
  RESULTADO: traza no graded (evidencia de evolucion del DSU en NetworkGame)
```

Lectura de la traza: en cada paso, `rootU`/`rootV` son las raices (antes de la
union) de los componentes de `u` y `v` segun el DSU interno de `NetworkGame`;
como las tres aristas conectan pares de vertices en componentes distintos,
las tres son aceptadas y `componentsAfter` desciende en 1 cada vez (6 -> 5 ->
4 -> 3), mientras `costAfter` acumula el peso de cada arista aceptada
(1 -> 3 -> 6).

---

## 4. Explicación de una arista rechazada

`[TODO: student written — do not fabricate]`

Instrucciones para el estudiante: elegir una arista real rechazada por
Kruskal en alguna de las corridas de la Seccion 1 o de la Seccion 2 (por
ejemplo, (0,3) o (4,6) en el Nivel 4; o cualquiera de las rechazadas en el
Modo 3 del Nivel 1) y explicar, con tus propias palabras, por que se rechazo
(que componente compartian sus extremos y por que aceptarla cerraria un
ciclo).

---

## 5. Arista segura y propiedad de corte

`[TODO: student written]`

Checklist a responder (spec Seccion 13 — "Relacion con la propiedad de
corte"), reproducido aqui como recordatorio, no como respuesta ya resuelta:

> En evidencia.md, el estudiante debera seleccionar una arista aceptada por
> Kruskal y explicar:
> 1. cuales eran los componentes antes de aceptarla;
> 2. que corte inducian;
> 3. que otras aristas cruzaban ese corte;
> 4. por que la arista aceptada podia considerarse ligera;
> 5. por que la propiedad de corte permite considerarla segura.
>
> Reto de aprendizaje: La respuesta "porque era la mas barata" no es
> suficiente. Debe indicar "mas barata entre que conjunto de aristas".

---

## 6. Cjugador vs CMST

Valores de `Cjugador` tomados de las corridas de consola reales de la Seccion
1 (columna "Corrida"); valores de `CMST` tomados de `game.Levels` (verificado
contra una ejecucion real de Kruskal, ver `src/game/Levels.java` y Prueba 6
de la Seccion 2, que confirma 11 y 13 para los Niveles 1 y 2 directamente con
`Kruskal.totalWeight`).

| Nivel | Corrida | Cjugador | CMST | Diferencia | Resultado |
|---|---|---|---|---|---|
| 1 (Tutorial) | Modo 1, `input-modo1-nivel1.txt` | 11 | 11 | 0 | OPTIMAL |
| 1 (Tutorial) | Modo 3, `input-modo3-nivel1.txt` | 11 | 11 | 0 | OPTIMAL (7/7 predicciones correctas) |
| 2 (Desafio) | Modo 2, `input-modo2-nivel2.txt` | 13 | 13 | 0 | OPTIMAL |
| 3 (Empates) | Modo 1, `input-modo1-nivel3.txt` | 19 | 19 | 0 | OPTIMAL (uno de 8 MST validos por empate) |
| 4 (Desconectado) | Modo 1, `input-modo1-nivel4.txt` | 12 (bosque de 5 aristas aceptadas) | No existe (grafo desconectado, 2 componentes) | N/A | IMPOSSIBLE — "No existe un MST global: el grafo es desconectado." |

---

## 7. Análisis de complejidad

`[TODO: reescribir con tus propias palabras]`

El siguiente es un **esqueleto factual de borrador** (tomado de la guia de la
materia, Secciones 16/20), no una respuesta terminada: el estudiante debe
reescribirlo con sus propias palabras y no entregarlo como copia literal.

- Sea `|V| = n`, `|E| = m`.
- Inicializacion del DSU: `O(n)`.
- Ordenar las `m` aristas: `O(m log m)`.
- Operaciones del DSU (Find/Union con union por rango + compresion de
  caminos): `O(m alpha(n))` amortizado.
- Costo total: `O(n) + O(m log m) + O(m alpha(n))`. El termino dominante es
  el ordenamiento, `O(E log E)` (usando `E = m`). La cota con `alpha(V)`
  conecta con la Sesion 12 (DSU) y deja claro que el DSU **no** domina el
  costo total.

`[TODO: el estudiante debe explicar aqui, con sus propias palabras, por que
el DSU no domina el costo total — es decir, por que `O(m alpha(n))` queda
dominado por `O(m log m)` para cualquier tamano de entrada practico, dado que
`alpha(n)` es esencialmente constante (< 5 para cualquier `n` representable)
mientras `log m` crece, aunque lentamente, sin cota.]`

---

## 8. Reflexión final (120-180 palabras)

`[TODO: student must personally write this]`

---
