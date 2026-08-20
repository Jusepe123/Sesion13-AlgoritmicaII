package console;

import game.NetworkGame;
import game.SelectionOutcome;
import model.Edge;
import model.Graph;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Modo 1 - Entrenamiento. En cada turno muestra el estado completo del
 * juego (aristas disponibles, costo acumulado y componentes restantes) y,
 * tras cada seleccion, imprime la explicacion completa de por que la arista
 * fue aceptada o rechazada, incluyendo la informacion de componentes.
 *
 * <p>Nunca importa {@code dsu.DisjointSet} ni {@code algorithm.Kruskal}: toda
 * decision del juego se obtiene exclusivamente de {@link NetworkGame#select}.</p>
 */
public class TrainingMode implements ModeRunner {

    @Override
    public NetworkGame run(Graph graph, Scanner in) {
        InputReader reader = new InputReader(in);
        NetworkGame game = new NetworkGame(graph);
        Set<Edge> usadas = new LinkedHashSet<>();

        ConsoleUI.banner("Modo 1: Entrenamiento");

        while (game.remainingComponents() > 1 && usadas.size() < graph.edges().size()) {
            ConsoleUI.section("Estado actual");
            System.out.println("Vertices: 0.." + (graph.vertexCount() - 1));
            System.out.println("Aristas:");
            ConsoleUI.printEdgeTable(graph);
            System.out.println("Costo acumulado: " + game.currentCost());
            System.out.println("Componentes restantes: " + game.remainingComponents());

            Edge elegida = elegirAristaNoUsada(reader, graph, usadas);
            usadas.add(elegida);

            SelectionOutcome outcome = game.select(elegida);
            imprimirResultado(outcome);
        }

        System.out.println();
        System.out.println("Partida finalizada.");
        return game;
    }

    private Edge elegirAristaNoUsada(InputReader reader, Graph graph, Set<Edge> usadas) {
        while (true) {
            Edge candidata = reader.readEdgeChoice(
                    "\nElige una arista (indice, o \"u v\"): ", graph);
            if (usadas.contains(candidata)) {
                System.out.println("  Esa arista ya fue utilizada. Elige otra.");
                continue;
            }
            return candidata;
        }
    }

    private void imprimirResultado(SelectionOutcome outcome) {
        Edge e = outcome.edge();
        System.out.println();
        System.out.println("Seleccion: " + e.u() + " -- " + e.v() + " (costo " + e.weight() + ")");
        System.out.println();
        if (outcome.accepted()) {
            ConsoleUI.success("ACEPTADA");
            System.out.println(e.u() + " y " + e.v() + " estaban en componentes distintos.");
        } else {
            ConsoleUI.failure("RECHAZADA");
            System.out.println(e.u() + " y " + e.v() + " ya pertenecen al mismo componente.");
            System.out.println("La arista cerraria un ciclo.");
        }
        System.out.println();
        System.out.println("Costo acumulado: " + outcome.costAfter());
        System.out.println("Componentes restantes: " + outcome.componentsAfter());
    }
}
