package app;

import algorithm.Kruskal;
import game.GameEvaluator;
import game.GameSummary;
import game.Levels;
import game.NetworkGame;
import game.SelectionOutcome;
import model.Edge;
import model.Graph;

import java.util.List;

/**
 * Programa de pruebas / evidencia automatizada de NetworkBuilder (Tarea 3).
 * Ejecuta y muestra evidencia de los 10 casos obligatorios (graded) mas una
 * traza adicional (no graded) que evidencia el comportamiento del DSU dentro
 * de {@link NetworkGame}.
 */
public class TestRunner {

    public static void main(String[] args) {
        prueba1SolucionOptima();
        prueba2SolucionFactibleNoOptima();
        prueba3IntentoDeCiclo();
        prueba4Empates();
        prueba5GrafoDesconectado();
        prueba6ComparacionKruskal();
        prueba7GrafoTrivialUnNodo();
        prueba8UnaSolaArista();
        prueba9PesosTodosIguales();
        prueba10ValidacionEntradas();
        pruebaTrazaDSU();
    }

    /**
     * Prueba 1: solucion optima. En el Nivel 2 (Desafio), el jugador elige
     * exactamente las 5 aristas del MST verificado (costo 13, en cualquier
     * orden) y debe quedar clasificado como OPTIMAL.
     */
    private static void prueba1SolucionOptima() {
        System.out.println("=== Prueba 1: solucion optima (Nivel 2) ===");
        Graph graph = Levels.level2Desafio();
        NetworkGame game = new NetworkGame(graph);

        int[][] elegidas = { {4, 5}, {1, 2}, {0, 1}, {2, 3}, {3, 4} };
        for (int[] par : elegidas) {
            Edge e = graph.findEdge(par[0], par[1]).orElseThrow();
            SelectionOutcome out = game.select(e);
            System.out.println("  select(" + e + ") -> accepted=" + out.accepted()
                    + ", costAfter=" + out.costAfter() + ", componentsAfter=" + out.componentsAfter());
        }

        int cost = game.currentCost();
        boolean connected = game.isConnected();
        GameSummary summary = GameEvaluator.evaluate(game);
        System.out.println("  currentCost() = " + cost + " (esperado 13)");
        System.out.println("  isConnected() = " + connected + " (esperado true)");
        System.out.println("  GameEvaluator.evaluate(game).outcome() = " + summary.outcome() + " (esperado OPTIMAL)");

        boolean ok = cost == 13 && connected && summary.outcome() == GameSummary.Outcome.OPTIMAL;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 2: solucion factible pero NO optima. Se elige un arbol de
     * expansion distinto (5 aristas, sin ciclos, conecta los 6 vertices) que
     * suma 16 en vez de 13. Verificado a mano (ver reporte de la tarea):
     * (0,1)+(1,2)+(2,3)+(3,5)+(4,5) forma un arbol de expansion valido de
     * costo 3+2+3+7+1 = 16.
     */
    private static void prueba2SolucionFactibleNoOptima() {
        System.out.println("\n=== Prueba 2: solucion factible pero no optima (Nivel 2) ===");
        Graph graph = Levels.level2Desafio();
        NetworkGame game = new NetworkGame(graph);

        int[][] elegidas = { {0, 1}, {1, 2}, {2, 3}, {3, 5}, {4, 5} };
        for (int[] par : elegidas) {
            Edge e = graph.findEdge(par[0], par[1]).orElseThrow();
            SelectionOutcome out = game.select(e);
            System.out.println("  select(" + e + ") -> accepted=" + out.accepted()
                    + ", costAfter=" + out.costAfter() + ", componentsAfter=" + out.componentsAfter());
        }

        boolean connected = game.isConnected();
        GameSummary summary = GameEvaluator.evaluate(game);
        System.out.println("  isConnected() = " + connected + " (esperado true)");
        System.out.println("  playerCost() = " + summary.playerCost() + " (esperado 16)");
        System.out.println("  mstCost() = " + summary.mstCost() + " (esperado 13)");
        System.out.println("  outcome() = " + summary.outcome() + " (esperado FEASIBLE_NOT_OPTIMAL)");

        boolean ok = connected
                && summary.outcome() == GameSummary.Outcome.FEASIBLE_NOT_OPTIMAL
                && summary.playerCost() == 16
                && summary.playerCost() > summary.mstCost();
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 3: intento de ciclo. Nivel 1: se aceptan (0,2,2) y (1,2,1),
     * que ya conectan a 0, 1 y 2 en la misma componente. Intentar (0,1,4)
     * despues debe ser rechazado por cerrar un ciclo.
     */
    private static void prueba3IntentoDeCiclo() {
        System.out.println("\n=== Prueba 3: intento de ciclo (Nivel 1) ===");
        Graph graph = Levels.level1Tutorial();
        NetworkGame game = new NetworkGame(graph);

        Edge e1 = graph.findEdge(0, 2).orElseThrow();
        Edge e2 = graph.findEdge(1, 2).orElseThrow();
        Edge e3 = graph.findEdge(0, 1).orElseThrow();

        SelectionOutcome out1 = game.select(e1);
        System.out.println("  select(" + e1 + ") -> accepted=" + out1.accepted());
        SelectionOutcome out2 = game.select(e2);
        System.out.println("  select(" + e2 + ") -> accepted=" + out2.accepted());
        SelectionOutcome out3 = game.select(e3);
        System.out.println("  select(" + e3 + ") -> accepted=" + out3.accepted()
                + ", alreadyConnected=" + out3.alreadyConnected()
                + ", rootU=" + out3.rootU() + ", rootV=" + out3.rootV());

        int cycleAttempts = game.cycleAttempts();
        System.out.println("  cycleAttempts() = " + cycleAttempts + " (esperado 1)");

        boolean ok = !out3.accepted() && out3.alreadyConnected() && cycleAttempts == 1;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 4: empates. Nivel 3 tiene MST verificado de costo 19 con 8
     * arboles distintos posibles por empates de peso. Se juegan DOS
     * selecciones de 5 aristas genuinamente distintas (una usa (2,4,5), la
     * otra usa (3,4,5)) y ambas deben quedar clasificadas OPTIMAL: la
     * comparacion es por costo, no por conjunto de aristas.
     */
    private static void prueba4Empates() {
        System.out.println("\n=== Prueba 4: empates (Nivel 3, dos MST distintos) ===");
        Graph graph = Levels.level3Empates();

        int[][] seleccionA = { {0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5} };
        NetworkGame gameA = new NetworkGame(graph);
        for (int[] par : seleccionA) {
            Edge e = graph.findEdge(par[0], par[1]).orElseThrow();
            gameA.select(e);
        }
        GameSummary summaryA = GameEvaluator.evaluate(gameA);
        System.out.println("  Seleccion A: " + gameA.selectedEdges());
        System.out.println("  costA = " + summaryA.playerCost() + " (esperado 19), outcome = " + summaryA.outcome());

        int[][] seleccionB = { {0, 1}, {0, 2}, {2, 3}, {3, 4}, {4, 5} };
        NetworkGame gameB = new NetworkGame(graph);
        for (int[] par : seleccionB) {
            Edge e = graph.findEdge(par[0], par[1]).orElseThrow();
            gameB.select(e);
        }
        GameSummary summaryB = GameEvaluator.evaluate(gameB);
        System.out.println("  Seleccion B: " + gameB.selectedEdges());
        System.out.println("  costB = " + summaryB.playerCost() + " (esperado 19), outcome = " + summaryB.outcome());

        boolean edgeSetsDiffer = !gameA.selectedEdges().equals(gameB.selectedEdges());
        System.out.println("  Los dos conjuntos de aristas son distintos: " + edgeSetsDiffer);

        boolean ok = edgeSetsDiffer
                && summaryA.playerCost() == 19 && summaryA.outcome() == GameSummary.Outcome.OPTIMAL
                && summaryB.playerCost() == 19 && summaryB.outcome() == GameSummary.Outcome.OPTIMAL;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 5: grafo desconectado. Nivel 4 tiene dos componentes
     * ({0,1,2,3} y {4,5,6}). Aceptando todas las aristas "aceptables" (sin
     * ciclo) del grafo se obtiene un bosque de 5 aristas (n-1 = 6 seria lo
     * necesario para un unico arbol de expansion). Kruskal.mst confirma el
     * mismo tamano de bosque, y el resultado del juego debe ser IMPOSSIBLE.
     */
    private static void prueba5GrafoDesconectado() {
        System.out.println("\n=== Prueba 5: grafo desconectado (Nivel 4) ===");
        Graph graph = Levels.level4Desconectado();
        NetworkGame game = new NetworkGame(graph);

        int accepted = 0;
        for (Edge e : graph.edges()) {
            SelectionOutcome out = game.select(e);
            System.out.println("  select(" + e + ") -> accepted=" + out.accepted()
                    + ", componentsAfter=" + out.componentsAfter());
            if (out.accepted()) {
                accepted++;
            }
        }
        System.out.println("  aristas aceptadas = " + accepted + " (esperado 5)");
        System.out.println("  remainingComponents() = " + game.remainingComponents() + " (esperado 2)");

        List<Edge> mst = Kruskal.mst(7, graph.edges());
        System.out.println("  Kruskal.mst(7, edges).size() = " + mst.size() + " (esperado 5, < n-1=6)");

        GameSummary summary = GameEvaluator.evaluate(game);
        System.out.println("  outcome() = " + summary.outcome() + " (esperado IMPOSSIBLE)");

        boolean ok = accepted == 5 && mst.size() == 5 && mst.size() < 6
                && summary.outcome() == GameSummary.Outcome.IMPOSSIBLE;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 6: comparacion directa con Kruskal. Se invoca Kruskal.mst /
     * totalWeight directamente sobre los grafos de los Niveles 1 y 2, sin
     * pasar por NetworkGame, y se confirman los costos ya verificados
     * (11 y 13 respectivamente).
     */
    private static void prueba6ComparacionKruskal() {
        System.out.println("\n=== Prueba 6: comparacion directa con Kruskal (Niveles 1 y 2) ===");
        Graph nivel1 = Levels.level1Tutorial();
        Graph nivel2 = Levels.level2Desafio();

        List<Edge> mst1 = Kruskal.mst(nivel1.vertexCount(), nivel1.edges());
        int total1 = Kruskal.totalWeight(mst1);
        System.out.println("  Nivel 1: mst=" + mst1);
        System.out.println("  Nivel 1: totalWeight = " + total1 + " (esperado 11)");

        List<Edge> mst2 = Kruskal.mst(nivel2.vertexCount(), nivel2.edges());
        int total2 = Kruskal.totalWeight(mst2);
        System.out.println("  Nivel 2: mst=" + mst2);
        System.out.println("  Nivel 2: totalWeight = " + total2 + " (esperado 13)");

        boolean ok = total1 == 11 && total2 == 13;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 7: grafo trivial de un solo nodo. Con n=1 y sin aristas, el
     * juego debe reportarse conectado desde el inicio (0 aristas son
     * necesarias) y GameEvaluator debe clasificarlo OPTIMAL con costo 0.
     */
    private static void prueba7GrafoTrivialUnNodo() {
        System.out.println("\n=== Prueba 7: grafo trivial de un solo nodo ===");
        Graph graph = new Graph(1, List.of());
        NetworkGame game = new NetworkGame(graph);

        boolean connected = game.isConnected();
        System.out.println("  isConnected() inmediatamente tras construir = " + connected + " (esperado true)");

        GameSummary summary = GameEvaluator.evaluate(game);
        System.out.println("  outcome() = " + summary.outcome() + " (esperado OPTIMAL)");
        System.out.println("  playerCost() = " + summary.playerCost() + " (esperado 0)");

        boolean ok = connected && summary.outcome() == GameSummary.Outcome.OPTIMAL && summary.playerCost() == 0;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 8: una sola arista. Con n=2 y una unica arista (0,1,5), al
     * aceptarla el costo del jugador debe coincidir trivialmente con el
     * costo del MST (la unica arista disponible es, por definicion, el
     * arbol de expansion minima).
     */
    private static void prueba8UnaSolaArista() {
        System.out.println("\n=== Prueba 8: una sola arista ===");
        Edge unica = new Edge(0, 1, 5);
        Graph graph = new Graph(2, List.of(unica));
        NetworkGame game = new NetworkGame(graph);

        SelectionOutcome out = game.select(unica);
        System.out.println("  select(" + unica + ") -> accepted=" + out.accepted() + ", costAfter=" + out.costAfter());

        GameSummary summary = GameEvaluator.evaluate(game);
        System.out.println("  playerCost() = " + summary.playerCost() + ", mstCost() = " + summary.mstCost() + " (ambos esperados 5)");
        System.out.println("  outcome() = " + summary.outcome() + " (esperado OPTIMAL)");

        boolean ok = out.accepted() && summary.playerCost() == 5 && summary.mstCost() == 5
                && summary.outcome() == GameSummary.Outcome.OPTIMAL;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 9: todos los pesos iguales. K4 (n=4) con las 6 aristas posibles,
     * todas de peso 10. Se aceptan 3 aristas cualesquiera que formen un
     * arbol de expansion (sin ciclo); sea cual sea el trio elegido, el
     * resultado debe ser OPTIMAL (todo arbol de expansion en un grafo con
     * pesos uniformes es un MST).
     */
    private static void prueba9PesosTodosIguales() {
        System.out.println("\n=== Prueba 9: pesos todos iguales (K4, peso 10) ===");
        List<Edge> todas = List.of(
                new Edge(0, 1, 10), new Edge(0, 2, 10), new Edge(0, 3, 10),
                new Edge(1, 2, 10), new Edge(1, 3, 10), new Edge(2, 3, 10)
        );
        Graph graph = new Graph(4, todas);
        NetworkGame game = new NetworkGame(graph);

        // Arbol en forma de "estrella" desde el vertice 0: sin ciclos, conecta los 4 vertices.
        Edge e1 = graph.findEdge(0, 1).orElseThrow();
        Edge e2 = graph.findEdge(0, 2).orElseThrow();
        Edge e3 = graph.findEdge(0, 3).orElseThrow();
        for (Edge e : List.of(e1, e2, e3)) {
            SelectionOutcome out = game.select(e);
            System.out.println("  select(" + e + ") -> accepted=" + out.accepted());
        }

        GameSummary summary = GameEvaluator.evaluate(game);
        System.out.println("  playerCost() = " + summary.playerCost() + " (esperado 30)");
        System.out.println("  outcome() = " + summary.outcome() + " (esperado OPTIMAL, sin importar el trio elegido)");

        boolean ok = summary.playerCost() == 30 && summary.outcome() == GameSummary.Outcome.OPTIMAL;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Prueba 10: validacion de entradas. Se confirma que se lanza
     * IllegalArgumentException en tres casos: n invalido (n=-1, para no dejar
     * ambiguedad con n=0), vertice fuera de rango, y lazo (self-loop).
     */
    private static void prueba10ValidacionEntradas() {
        System.out.println("\n=== Prueba 10: validacion de entradas ===");

        boolean lanzoNInvalido = false;
        try {
            new Graph(-1, List.of());
        } catch (IllegalArgumentException ex) {
            lanzoNInvalido = true;
            System.out.println("  new Graph(-1, []) lanzo IllegalArgumentException: " + ex.getMessage());
        }
        if (!lanzoNInvalido) {
            System.out.println("  new Graph(-1, []) NO lanzo excepcion (inesperado)");
        }

        boolean lanzoVerticeFueraDeRango = false;
        try {
            new Graph(3, List.of(new Edge(0, 5, 1)));
        } catch (IllegalArgumentException ex) {
            lanzoVerticeFueraDeRango = true;
            System.out.println("  new Graph(3, [(0,5,1)]) lanzo IllegalArgumentException: " + ex.getMessage());
        }
        if (!lanzoVerticeFueraDeRango) {
            System.out.println("  new Graph(3, [(0,5,1)]) NO lanzo excepcion (inesperado)");
        }

        boolean lanzoLazo = false;
        try {
            new Edge(2, 2, 5);
        } catch (IllegalArgumentException ex) {
            lanzoLazo = true;
            System.out.println("  new Edge(2,2,5) lanzo IllegalArgumentException: " + ex.getMessage());
        }
        if (!lanzoLazo) {
            System.out.println("  new Edge(2,2,5) NO lanzo excepcion (inesperado)");
        }

        boolean ok = lanzoNInvalido && lanzoVerticeFueraDeRango && lanzoLazo;
        System.out.println("  RESULTADO: " + (ok ? "PASA" : "FALLA"));
    }

    /**
     * Traza DSU (no graded, puramente evidencial): en el Nivel 2, se llama a
     * select() para 3 aristas consecutivas y se imprime, tras cada llamada,
     * el estado del DSU expuesto por SelectionOutcome (rootU, rootV,
     * componentsAfter) para que la evolucion del DSU quede documentada como
     * evidencia (evidencia.md Seccion 3). Analogo en espiritu a
     * prueba4TrazadoCompresion de Sesion12, pero usando la API disponible en
     * NetworkGame/SelectionOutcome en vez de parentArray().
     */
    private static void pruebaTrazaDSU() {
        System.out.println("\n=== Traza DSU (evidencia, Nivel 2) ===");
        Graph graph = Levels.level2Desafio();
        NetworkGame game = new NetworkGame(graph);

        int[][] pares = { {4, 5}, {1, 2}, {0, 1} };
        int paso = 1;
        for (int[] par : pares) {
            Edge e = graph.findEdge(par[0], par[1]).orElseThrow();
            System.out.println("  --- Paso " + paso + ": select(" + e + ") ---");
            System.out.println("    ANTES:  remainingComponents() = " + game.remainingComponents());
            SelectionOutcome out = game.select(e);
            System.out.println("    DESPUES: accepted=" + out.accepted()
                    + ", rootU=" + out.rootU()
                    + ", rootV=" + out.rootV()
                    + ", componentsAfter=" + out.componentsAfter()
                    + ", costAfter=" + out.costAfter());
            paso++;
        }
        System.out.println("  RESULTADO: traza no graded (evidencia de evolucion del DSU en NetworkGame)");
    }
}
