package game;

import model.Edge;

import java.util.List;

/**
 * Resumen final (o parcial) de una partida, comparando al jugador contra el
 * optimo del arbol de expansion minima.
 *
 * <p>{@code score} y {@code scoreClassification} son {@code null} siempre que
 * {@code !graphConnected || !playerConnected}: un puntaje numerico no tiene
 * sentido cuando el grafo de origen es infactible ({@code IMPOSSIBLE}) o
 * cuando el jugador aun no termino de conectar la red ({@code INCOMPLETE}).
 * Los consumidores (consola/GUI) deben mostrar "N/A" en esos casos en vez de
 * inventar un numero.</p>
 */
public record GameSummary(
        int playerCost,
        int mstCost,
        List<Edge> playerEdges,
        List<Edge> mstEdges,
        boolean graphConnected,
        boolean playerConnected,
        Outcome outcome,
        Integer score,
        String scoreClassification
) {
    public enum Outcome { OPTIMAL, FEASIBLE_NOT_OPTIMAL, INCOMPLETE, IMPOSSIBLE }
}
