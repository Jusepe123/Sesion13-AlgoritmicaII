package console;

import game.NetworkGame;
import game.SelectionOutcome;
import model.Edge;
import model.Graph;

import java.util.List;
import java.util.Scanner;

/**
 * Modo 3 - Entrenador de Kruskal. Presenta TODAS las aristas del grafo
 * ordenadas por peso ascendente (via {@link Graph#edgesSortedByWeight()},
 * un ordenamiento simple) y, para cada una, le pide al jugador que adivine
 * si Kruskal la aceptaria o la rechazaria antes de revelar la respuesta real.
 *
 * <p>Restriccion global de esta tarea: esta clase NUNCA debe invocar
 * {@code algorithm.Kruskal.mst} ni ninguna otra funcion de esa clase. El
 * grafo se recorre en orden de peso (imitando el orden que usaria Kruskal)
 * pero la decision real de aceptar/rechazar cada arista se obtiene siempre
 * llamando a {@link NetworkGame#select}, que es el unico lugar autorizado a
 * consultar el DSU. Tampoco importa {@code dsu.DisjointSet} directamente.</p>
 */
public class KruskalTrainerMode implements ModeRunner {

    @Override
    public NetworkGame run(Graph graph, Scanner in) {
        InputReader reader = new InputReader(in);
        NetworkGame game = new NetworkGame(graph);

        ConsoleUI.banner("Modo 3: Entrenador de Kruskal");
        System.out.println("Para cada arista, en orden ascendente de peso, adivina si Kruskal la");
        System.out.println("aceptaria (A) o la rechazaria (R). Luego se revela la respuesta real.");

        List<Edge> ordenadas = graph.edgesSortedByWeight();
        int correctPredictions = 0;
        int wrongPredictions = 0;

        int turno = 1;
        for (Edge e : ordenadas) {
            System.out.println();
            System.out.println("Arista " + turno + "/" + ordenadas.size() + ": " + ConsoleUI.edgeLabel(e));
            boolean guessAccept = reader.readAcceptReject("A) Aceptar / R) Rechazar: ");

            SelectionOutcome outcome = game.select(e);
            boolean correctIsAccept = outcome.accepted();

            System.out.println("  Extremos " + e.u() + " y " + e.v()
                    + (outcome.alreadyConnected() ? " ya estaban en el mismo componente." : " estaban en componentes distintos."));
            System.out.println("  Decision correcta: " + (correctIsAccept ? "ACEPTAR" : "RECHAZAR")
                    + " (Kruskal la " + (correctIsAccept ? "acepta" : "rechaza") + ")");
            System.out.println("  Tu respuesta: " + (guessAccept ? "ACEPTAR" : "RECHAZAR"));

            if (guessAccept == correctIsAccept) {
                correctPredictions++;
                ConsoleUI.success("  Correcto!");
            } else {
                wrongPredictions++;
                ConsoleUI.failure("  Incorrecto.");
            }

            System.out.println("  Costo acumulado: " + outcome.costAfter()
                    + " | Componentes restantes: " + outcome.componentsAfter());
            turno++;
        }

        ConsoleUI.section("Resumen del entrenador");
        System.out.println("Aristas evaluadas: " + ordenadas.size());
        System.out.println("Predicciones correctas: " + correctPredictions);
        System.out.println("Predicciones incorrectas: " + wrongPredictions);
        System.out.println("Intentos de ciclo reales (game.cycleAttempts()): " + game.cycleAttempts());

        return game;
    }
}
