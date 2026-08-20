package game;

import model.Edge;

/**
 * Resultado de un intento de seleccionar una arista en {@link NetworkGame}.
 *
 * <p>Incluye tanto el veredicto ({@code accepted}) como la informacion de
 * diagnostico usada para explicarle al jugador por que una arista fue
 * rechazada (raices de DSU antes de la union, y el estado del juego
 * despues del intento).</p>
 */
public record SelectionOutcome(
        Edge edge,
        boolean accepted,
        boolean alreadyConnected,
        int rootU,
        int rootV,
        int costAfter,
        int componentsAfter
) {}
