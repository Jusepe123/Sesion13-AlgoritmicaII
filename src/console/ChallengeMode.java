package console;

import game.NetworkGame;
import game.SelectionOutcome;
import model.Edge;
import model.Graph;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Modo 2 - Desafio. Misma mecanica de turnos que {@link TrainingMode}, pero
 * oculta informacion de componentes: antes de elegir no se muestra cuantas
 * componentes quedan, y tras la seleccion solo se informa si fue aceptada o
 * rechazada junto con el costo acumulado y las componentes restantes (nunca
 * la explicacion de que vertices comparten componente).
 *
 * <p>Nunca importa {@code dsu.DisjointSet} ni {@code algorithm.Kruskal}: toda
 * decision del juego se obtiene exclusivamente de {@link NetworkGame#select}.</p>
 */
public class ChallengeMode implements ModeRunner {

    @Override
    public NetworkGame run(Graph graph, Scanner in) {
        InputReader reader = new InputReader(in);
        NetworkGame game = new NetworkGame(graph);
        Set<Edge> usadas = new LinkedHashSet<>();

        ConsoleUI.banner("Modo 2: Desafio");

        while (game.remainingComponents() > 1 && usadas.size() < graph.edges().size()) {
            ConsoleUI.section("Estado actual");
            System.out.println("Vertices: 0.." + (graph.vertexCount() - 1));
            System.out.println("Aristas:");
            ConsoleUI.printEdgeTable(graph);
            System.out.println("Costo acumulado: " + game.currentCost());

            Edge elegida = elegirAristaNoUsada(reader, graph, usadas);
            if (elegida == null) {
                // Entrada agotada (stdin redirigido que se quedo corto) mientras
                // reintentabamos por una arista ya usada: no hay forma de seguir
                // pidiendo una eleccion valida, asi que cortamos la partida aqui
                // en vez de reintentar para siempre sobre el mismo repliegue.
                System.out.println();
                System.out.println("Entrada agotada. Terminando la partida.");
                break;
            }
            usadas.add(elegida);

            SelectionOutcome outcome = game.select(elegida);
            imprimirResultado(outcome);
        }

        System.out.println();
        System.out.println("Partida finalizada.");
        return game;
    }

    /**
     * Pide una arista no usada aun. Devuelve {@code null} si la entrada se
     * agota (ver {@link InputReader#isExhausted()}) antes de conseguir una
     * arista valida: en ese caso no reintentamos (reintentar devolveria
     * siempre el mismo repliegue de {@link InputReader#readEdgeChoice} y
     * colgaria el programa), sino que le indicamos al llamador que corte el
     * bucle de turnos.
     */
    private Edge elegirAristaNoUsada(InputReader reader, Graph graph, Set<Edge> usadas) {
        while (true) {
            Edge candidata = reader.readEdgeChoice(
                    "\nElige una arista (indice, o \"u v\"): ", graph);
            if (reader.isExhausted()) {
                return null;
            }
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
        } else {
            ConsoleUI.failure("RECHAZADA");
        }
        System.out.println();
        System.out.println("Costo acumulado: " + outcome.costAfter());
        System.out.println("Componentes restantes: " + outcome.componentsAfter());
    }
}
