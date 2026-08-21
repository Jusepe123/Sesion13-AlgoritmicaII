# Registro de colaboración — NetworkBuilder

NetworkBuilder fue desarrollado como un trabajo colaborativo entre el estudiante
y asistentes de programación con IA. Para el núcleo del proyecto se utilizó
**Claude Code** como apoyo en la implementación de Java, el algoritmo, las pruebas,
la consola, la primera interfaz JavaFX y la documentación. En una fase posterior
se trabajó con **Codex** para revisar e iterar el diseño visual de JavaFX.

El estudiante definió el objetivo, tomó las decisiones académicas y de diseño,
revisó el código, probó la experiencia de uso y decidió qué propuestas se
conservaban. Los asistentes apoyaron con alternativas técnicas, implementación,
refactorización y ejecución de verificaciones.

Este documento no atribuye el proyecto completo a ninguna de las dos partes. Las
decisiones surgieron de un ciclo de trabajo conjunto:

1. El estudiante planteó una necesidad o evaluó el resultado visual.
2. El asistente propuso e implementó una solución concreta.
3. El estudiante probó la interfaz y señaló nuevos problemas o preferencias.
4. La solución se ajustó y se verificó mediante compilación y pruebas.

## Responsabilidades durante la colaboración

### Aportes del estudiante

- Definición del tema: construcción de redes y algoritmo de Kruskal.
- Elección de los modos de entrenamiento y desafío.
- Evaluación visual de la aplicación en una pantalla real.
- Detección de contenido recortado al abrir la comparación.
- Decisión de separar las instrucciones del área donde se dibuja el grafo.
- Solicitud de un menú principal más sencillo y con menos texto.
- Rechazo de ilustraciones que no coincidían con el estilo deseado.
- Solicitud de una bitácora más clara y fácil de interpretar.
- Aprobación o corrección de cada iteración de diseño.

### Apoyo de Claude Code y Codex

- Inspección de la estructura Java y JavaFX existente.
- Propuestas de composición, jerarquía visual, colores y animaciones.
- Implementación de componentes JavaFX y estilos CSS.
- Integración de recursos gráficos dentro del proceso de compilación.
- Conservación de la lógica del juego fuera de la capa gráfica.
- Compilación de la aplicación después de cada cambio.
- Ejecución de las diez pruebas automatizadas para comprobar que la interfaz no
  alteró el funcionamiento del algoritmo.

## Prompts de desarrollo del código con Claude Code

Esta sección documenta la colaboración usada para construir el núcleo. Cuando no
se conservó el texto literal de una consulta, el prompt se presenta como un
**resumen fiel y viable**, no como una cita textual. La estructura final puede
contrastarse con `src/`, `README.md` y `evidencia.md`.

### 1. Estructuras de datos del grafo

**Necesidad:** representar aristas ponderadas y grafos sin mezclar la entrada de
usuario con la lógica del algoritmo.

**Prompt resumido para Claude Code:**

> Crea en Java 21 un modelo inmutable para una arista ponderada y una clase Graph
> que valide cantidad de vértices, extremos fuera de rango y lazos. Mantén estas
> clases independientes de la consola y JavaFX.

**Trabajo conjunto:** el estudiante definió las restricciones del ejercicio y
revisó la representación elegida. Claude Code apoyó con la implementación de
`Edge` y `Graph` y sus validaciones.

**Decisión técnica:** usar un `record Edge` y conservar la lista de aristas dentro
de `Graph`, validando los datos al construir los objetos.

**Verificación:** las pruebas de entrada inválida comprueban vértices negativos,
extremos fuera de rango y aristas con `u == v`.

### 2. Disjoint Set Union

**Necesidad:** detectar eficientemente si una arista conecta componentes distintos
o forma un ciclo.

**Prompt resumido para Claude Code:**

> Integra un Disjoint Set Union con compresión de caminos y unión por rango. Expón
> operaciones find, union y components, y explica cómo se utilizará para detectar
> ciclos durante Kruskal.

**Trabajo conjunto:** el estudiante aportó el DSU trabajado previamente en la
Sesión 12 y Claude Code ayudó a integrarlo y documentarlo dentro del nuevo
proyecto.

**Decisión técnica:** reutilizar el DSU de la sesión anterior en `src/dsu` en vez
de duplicar la detección de ciclos en las interfaces.

**Verificación:** `TestRunner` incluye una traza de tres uniones que muestra la
reducción de componentes y las raíces antes de aplicar cada unión.

### 3. Algoritmo de Kruskal

**Necesidad:** calcular una solución óptima real para comparar las decisiones del
jugador.

**Prompt resumido para Claude Code:**

> Implementa Kruskal usando el DSU existente. Ordena las aristas por peso, acepta
> únicamente las que unan componentes diferentes y devuelve las aristas elegidas
> y su costo. Considera empates y grafos desconectados.

**Trabajo conjunto:** el estudiante definió los casos que debían evaluarse y
Claude Code apoyó con `Kruskal`, `KruskalResult` y la separación entre selección
de aristas y cálculo del peso total.

**Decisión técnica:** el algoritmo devuelve un bosque cuando el grafo es
desconectado; `GameEvaluator` es quien interpreta que no existe un MST global.

**Verificación:** se comprobaron costos 11 y 13 para los niveles 1 y 2, dos MST
distintos de costo 19 en el nivel con empates y un bosque de cinco aristas en el
nivel desconectado.

### 4. Motor del juego y puntuación

**Necesidad:** convertir Kruskal en un juego sin colocar reglas algorítmicas dentro
de la interfaz.

**Prompt resumido para Claude Code:**

> Diseña un NetworkGame que reciba elecciones de aristas, use DSU para aceptarlas
> o rechazarlas y registre costo, componentes e intentos de ciclo. Añade un
> evaluador que compare la red final contra Kruskal y una puntuación de 0 a 100.

**Trabajo conjunto:** el estudiante estableció la mecánica y las clasificaciones.
Claude Code apoyó con `NetworkGame`, `SelectionOutcome`, `GameEvaluator`,
`GameSummary` y `ScoreCalculator`.

**Decisión técnica:** devolver un `SelectionOutcome` con toda la información de
cada turno permite que consola y JavaFX narren la misma decisión sin recalcularla.

**Verificación:** las pruebas cubren soluciones óptimas, redes completas no
óptimas, ciclos, partidas incompletas, grafos imposibles y casos triviales.

### 5. Niveles y aplicación de consola

**Necesidad:** ofrecer distintos escenarios y tres formas de aprendizaje.

**Prompt resumido para Claude Code:**

> Crea cuatro niveles reproducibles y una consola con modo Entrenamiento, modo
> Desafío y Entrenador de Kruskal. Centraliza la lectura de entradas y evita
> duplicar la lógica de aceptación de aristas entre los modos.

**Trabajo conjunto:** el estudiante eligió el enfoque educativo y revisó los
grafos de cada nivel. Claude Code apoyó con `Levels`, `ConsoleUI`, `InputReader`,
los tres `ModeRunner` y el punto de entrada `app.Main`.

**Decisión técnica:** cada modo controla solamente qué información presenta; las
decisiones siguen perteneciendo al motor o al algoritmo.

**Verificación:** se guardaron partidas reproducibles en `scripts/inputs` y sus
salidas están registradas por nivel en `evidencia.md`.

### 6. Pruebas automatizadas

**Necesidad:** demostrar que el proyecto satisface casos normales, empates,
ciclos, desconexión y validación de entradas.

**Prompt resumido para Claude Code:**

> Construye un TestRunner sin dependencias externas que ejecute diez casos,
> muestre valores esperados y obtenidos, y falle con un exit code distinto de cero
> si alguna condición no se cumple. Incluye una traza educativa del DSU.

**Trabajo conjunto:** el estudiante definió qué resultados debían demostrarse y
Claude Code apoyó con la automatización y el formato reproducible de la salida.

**Decisión técnica:** utilizar Java puro permite ejecutar la evidencia sin Maven,
Gradle ni una biblioteca de pruebas adicional.

**Verificación:** `scripts\run-tests.ps1` muestra diez líneas `RESULTADO: PASA` y
la tabla completa se conserva en `evidencia.md`.

### 7. Scripts, JavaFX inicial y documentación

**Necesidad:** facilitar la compilación en Windows y sistemas POSIX, manteniendo
JavaFX como una extensión opcional.

**Prompt resumido para Claude Code:**

> Prepara scripts para compilar, ejecutar consola, correr pruebas y descargar o
> localizar JavaFX. Mantén `src/` compilable sin JavaFX y coloca la interfaz en
> `src-gui/`. Documenta comandos, estructura, evidencia y complejidad.

**Trabajo conjunto:** el estudiante organizó el entregable y verificó los comandos
en su entorno. Claude Code apoyó con los scripts `.ps1`/`.sh`, la primera versión
de la interfaz y la documentación técnica.

**Decisión técnica:** producir `out/` y `out-gui/` por separado evita que el bonus
gráfico se convierta en una dependencia del núcleo calificado.

**Verificación:** los scripts compilan 21 clases del núcleo sin módulos JavaFX y
25 clases al incluir la interfaz; `README.md` contiene los pasos reproducibles.

## Prompts utilizados durante la mejora visual con Codex

Las siguientes entradas son resúmenes fieles de solicitudes realizadas durante
la sesión. Las respuestas fueron revisadas, corregidas y verificadas.

### 1. Mejora general de JavaFX

**Problema:** la interfaz funcionaba, pero tenía poca jerarquía visual y dependía
de estilos escritos directamente dentro de las clases Java.

**Prompt:**

> Find a way to improve the UI of this project; it uses the JavaFX library.

**Trabajo conjunto:** el asistente propuso un tema oscuro consistente, tarjetas
para las métricas, una cabecera y una leyenda. El estudiante revisó el resultado
en la aplicación y continuó indicando los puntos que necesitaban ajustes.

**Decisión:** trasladar la mayor parte de la apariencia a `theme.css` y mantener
en Java únicamente la estructura y el comportamiento de los controles.

**Verificación:** `scripts\compile-gui.ps1` y `scripts\run-tests.ps1`; la
aplicación compiló y las diez pruebas pasaron.

### 2. Contenido recortado en pantallas pequeñas

**Problema:** al abrir la comparación con Kruskal, el panel lateral perdía altura
y sus controles inferiores dejaban de ser accesibles.

**Prompt:**

> Fix it. I cannot see all the information, and my screen does not allow me to
> scroll down when I want to compare.

**Trabajo conjunto:** el problema fue observado por el estudiante en uso real. El
asistente localizó la causa en el `VBox` lateral y lo envolvió en un
`ScrollPane` vertical.

**Decisión:** usar desplazamiento solo cuando sea necesario y desactivar la barra
horizontal para conservar el ancho del panel.

**Verificación:** recompilación de JavaFX, comprobación del recurso CSS y ejecución
completa de las pruebas.

### 3. Instrucciones encima del grafo

**Problema:** el texto “Selecciona una arista” estaba superpuesto sobre las
aristas del dibujo.

**Prompt:**

> “Selecciona una arista” shows up in front of the branches.

**Trabajo conjunto:** el estudiante identificó la interferencia visual. El
asistente movió la instrucción y la leyenda a una cabecera independiente sobre
el lienzo.

**Decisión:** reservar el `Canvas` exclusivamente para vértices y aristas.

**Verificación:** compilación exitosa y diez pruebas aprobadas.

### 4. Menú principal y movimiento

**Problema:** la aplicación comenzaba directamente en el juego y se sentía más
como una demostración técnica que como una experiencia completa.

**Prompt:**

> Improve the UI more. Maybe add a principal menu or more animation; make a
> better design.

**Trabajo conjunto:** se añadió un menú para seleccionar modo, una acción para
volver al menú, transiciones suaves y animación al mostrar la comparación. El
estudiante pidió posteriormente reducir el texto y ajustar varias veces la
ilustración central.

**Decisión:** conservar animaciones breves y funcionales, evitando que oculten la
información del algoritmo.

**Verificación:** compilación del proyecto gráfico y ejecución de las pruebas del
motor después de los cambios.

### 5. Simplificación del menú y su ilustración

**Problema:** el menú contenía demasiado texto y las primeras ilustraciones no
coincidían con el estilo esperado.

**Prompts:**

> Make a better menu. It has too much text; maybe put an image in the center.

> The picture is unnatural. Make it simple and minimalistic.

> Change the picture from the principal menu; I don't like it.

**Trabajo conjunto:** el estudiante dirigió el resultado mediante retroalimentación
concreta y rechazó dos propuestas. El asistente redujo el contenido a un título
corto y dos botones, e integró una nueva ilustración editorial de islas conectadas.

**Decisión:** usar una imagen sin texto, con fondo transparente y una metáfora de
conexión, en lugar de repetir literalmente el grafo de la pantalla de juego.

**Verificación:** se confirmó que `network-hero.png` se copiara a `out-gui` en
Windows y se actualizó también el script POSIX. La aplicación compiló correctamente.

### 6. Bitácora comprensible

**Problema:** la bitácora era un bloque continuo de texto y resultaba difícil
distinguir rápidamente una aceptación, un ciclo o una advertencia.

**Prompt:**

> Fix the way the text is shown in Bitácora. Make it better and easier to
> understand.

**Trabajo conjunto:** el estudiante señaló el problema de lectura. El asistente
reemplazó el `TextArea` por una lista de tarjetas, simplificó los mensajes y añadió
indicadores visuales por tipo de evento.

**Decisión:** mostrar primero el resultado de cada acción y después los datos de
costo y componentes.

**Verificación:** compilación exitosa y diez pruebas aprobadas.

### 7. Indicador de progreso y celebración

**Problema:** faltaba una respuesta visual clara sobre cuánto faltaba para terminar
la red y el final de una partida no se sentía especial.

**Prompt:**

> Make any other impressive changes to the UI so it will be remarkable. You
> have complete freedom.

**Trabajo conjunto:** con la libertad otorgada por el estudiante, el asistente
propuso un medidor de conexiones, transiciones de nivel y una celebración breve.
El progreso se calcula únicamente a partir del estado público de `NetworkGame`.

**Decisión:** añadir efectos solo en la capa visual, sin introducir lógica de MST
en el paquete `gui`.

**Verificación:** compilación de las 25 clases Java y ejecución satisfactoria de
las diez pruebas automatizadas.

## Prompts posibles para continuar el proyecto

Estos prompts no se presentan como interacciones ya realizadas. Son solicitudes
viables para futuras sesiones; sus resultados también deben revisarse y probarse.

### Accesibilidad

> Revisa la interfaz JavaFX para navegación completa con teclado. Añade indicadores
> de foco visibles y texto accesible, sin cambiar la lógica de NetworkGame.

> Comprueba el contraste de `theme.css` y propone ajustes que cumplan WCAG AA para
> texto normal y controles interactivos.

### Adaptación a diferentes pantallas

> Haz que la ventana JavaFX funcione correctamente desde 880×640 hasta 1920×1080.
> Identifica controles recortados y usa contenedores redimensionables o scroll solo
> donde sea necesario.

### Experiencia de aprendizaje

> Añade una ayuda inicial de tres pasos que explique vértices, pesos y ciclos. Debe
> poder omitirse y no debe revelar el MST en modo Desafío.

> Diseña mensajes de retroalimentación que expliquen por qué una arista fue
> rechazada, usando exclusivamente los datos de SelectionOutcome.

### Calidad y mantenimiento

> Revisa las clases de `src-gui/gui` y localiza estilos inline que todavía puedan
> trasladarse a `theme.css`. No modifiques el motor en `src/game`.

> Añade pruebas de humo para construir los componentes JavaFX sin abrir una ventana,
> y conserva las pruebas actuales del algoritmo.

> Verifica que los scripts de PowerShell y POSIX empaqueten todos los recursos CSS
> e imágenes requeridos por GameApp.

## Criterio de uso responsable

Una sugerencia de IA no se considera correcta por el hecho de estar bien escrita.
Para este proyecto, cada cambio debe pasar al menos por estas comprobaciones:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\compile-gui.ps1
powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1
```

Además, los cambios visuales deben probarse manualmente porque una compilación no
permite detectar por sí sola texto recortado, controles inaccesibles, animaciones
molestas o una composición que no funcione en la pantalla del usuario.
