package game;

import algorithm.Kruskal;
import model.Edge;
import model.Graph;

import java.util.List;

/**
 * Compara una partida jugada ({@link NetworkGame}) contra el optimo real
 * calculado con Kruskal.
 *
 * <p>Este es el UNICO archivo del paquete {@code game} autorizado a importar
 * o invocar {@code algorithm.Kruskal}. {@link NetworkGame} nunca debe verse
 * involucrado en este calculo mientras el jugador esta jugando.</p>
 *
 * <p>La comparacion jugador-vs-optimo se hace estrictamente por costo
 * ({@code game.currentCost() == mstCost}), nunca comparando conjuntos de
 * aristas: en grafos con empates (varios MST validos de igual costo) el
 * jugador puede elegir un MST distinto del que devuelve Kruskal y aun asi
 * ser {@code OPTIMAL}.</p>
 */
public final class GameEvaluator {

    private GameEvaluator() {}

    public static GameSummary evaluate(NetworkGame game) {
        Graph graph = game.graph();
        List<Edge> mstEdges = Kruskal.mst(graph.vertexCount(), graph.edges());
        int mstCost = Kruskal.totalWeight(mstEdges);
        boolean graphConnected = Kruskal.isSpanning(graph.vertexCount(), mstEdges);
        boolean playerConnected = game.isConnected();

        GameSummary.Outcome outcome;
        Integer score = null;
        String classification = null;
        if (!graphConnected) {
            outcome = GameSummary.Outcome.IMPOSSIBLE;
        } else if (!playerConnected) {
            outcome = GameSummary.Outcome.INCOMPLETE;
        } else {
            outcome = (game.currentCost() == mstCost)
                    ? GameSummary.Outcome.OPTIMAL
                    : GameSummary.Outcome.FEASIBLE_NOT_OPTIMAL;
            score = ScoreCalculator.score(game.currentCost(), mstCost, game.cycleAttempts());
            classification = ScoreCalculator.classify(score);
        }
        return new GameSummary(game.currentCost(), mstCost, game.selectedEdges(), mstEdges,
                graphConnected, playerConnected, outcome, score, classification);
    }
}
