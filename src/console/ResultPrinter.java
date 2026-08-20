package console;

import game.GameSummary;
import model.Edge;

import java.util.List;

/**
 * Imprime el bloque {@code === RESULTADO ===} compartido por los tres modos,
 * a partir de un {@link GameSummary} ya calculado por {@code game.GameEvaluator}.
 * No calcula nada por si mismo: solo formatea los datos del resumen.
 */
public final class ResultPrinter {

    private ResultPrinter() {}

    public static void print(GameSummary summary) {
        System.out.println();
        System.out.println("=== RESULTADO ===");
        System.out.println();

        switch (summary.outcome()) {
            case IMPOSSIBLE -> printImpossible(summary);
            case INCOMPLETE -> printIncomplete(summary);
            case OPTIMAL -> printOptimal(summary);
            case FEASIBLE_NOT_OPTIMAL -> printFeasibleNotOptimal(summary);
        }
    }

    private static void printImpossible(GameSummary summary) {
        // Redaccion literal exigida por el enunciado (spec Section 10.4).
        ConsoleUI.failure("No existe un MST global: el grafo es desconectado.");
        System.out.println();
        System.out.println("El grafo de origen no es conexo: ningun jugador puede completar");
        System.out.println("una red que abarque todos los vertices en un solo componente.");
        System.out.println("Aristas seleccionadas por el jugador: " + summary.playerEdges().size());
        printScoreLines(summary);
    }

    private static void printIncomplete(GameSummary summary) {
        System.out.println("Partida incompleta: la red aun no conecta todos los vertices.");
        System.out.println();
        System.out.println("Costo acumulado hasta el momento: " + summary.playerCost());
        System.out.println("Costo optimo MST: " + summary.mstCost());
        printScoreLines(summary);
    }

    private static void printOptimal(GameSummary summary) {
        System.out.println("Costo del jugador: " + summary.playerCost());
        System.out.println("Costo optimo MST: " + summary.mstCost());
        System.out.println();
        ConsoleUI.success("EXCELENTE:");
        System.out.println("La red construida es un MST.");
        printScoreLines(summary);
    }

    private static void printFeasibleNotOptimal(GameSummary summary) {
        int diff = summary.playerCost() - summary.mstCost();
        System.out.println("Costo del jugador: " + summary.playerCost());
        System.out.println("Costo optimo MST: " + summary.mstCost());
        System.out.println("Diferencia: +" + diff);
        System.out.println();
        System.out.println("Estado:");
        System.out.println(ConsoleUI.colorize("RED COMPLETA, PERO NO OPTIMA", ConsoleUI.ANSI_YELLOW));
        System.out.println();
        System.out.println("Aristas seleccionadas:");
        printEdgeList(summary.playerEdges());
        System.out.println();
        System.out.println("MST de Kruskal:");
        printEdgeList(summary.mstEdges());
        printScoreLines(summary);
    }

    private static void printEdgeList(List<Edge> edges) {
        for (Edge e : edges) {
            System.out.println("  " + ConsoleUI.edgeLabel(e));
        }
    }

    private static void printScoreLines(GameSummary summary) {
        System.out.println();
        System.out.println("Puntaje: " + (summary.score() == null ? "N/A" : summary.score()));
        System.out.println("Clasificacion: " + (summary.scoreClassification() == null ? "N/A" : summary.scoreClassification()));
    }
}
