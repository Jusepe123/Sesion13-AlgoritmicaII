package game;

import model.Edge;
import model.Graph;

import java.util.List;

/**
 * Fabrica estatica de los niveles del juego. Los datos de cada nivel ya fueron
 * verificados contra una ejecucion real de Kruskal (ver task-1-brief.md); no
 * alterar los numeros.
 */
public final class Levels {

    private Levels() {}

    /** n=5. Costo de MST verificado: 11. */
    public static Graph level1Tutorial() {
        return new Graph(5, List.of(
                new Edge(0, 1, 4),
                new Edge(0, 2, 2),
                new Edge(1, 2, 1),
                new Edge(3, 4, 3),
                new Edge(1, 3, 7),
                new Edge(2, 3, 5),
                new Edge(2, 4, 8)
        ));
    }

    /** n=6. Costo de MST verificado: 13. */
    public static Graph level2Desafio() {
        return new Graph(6, List.of(
                new Edge(0, 1, 3),
                new Edge(0, 2, 6),
                new Edge(1, 2, 2),
                new Edge(1, 3, 5),
                new Edge(2, 3, 3),
                new Edge(2, 4, 5),
                new Edge(3, 4, 4),
                new Edge(3, 5, 7),
                new Edge(4, 5, 1),
                new Edge(1, 5, 9)
        ));
    }

    /** n=6. Costo de MST verificado: 19, con 8 MST distintos validos (empates). */
    public static Graph level3Empates() {
        return new Graph(6, List.of(
                new Edge(0, 1, 2),
                new Edge(0, 2, 2),
                new Edge(1, 2, 3),
                new Edge(1, 3, 4),
                new Edge(2, 3, 4),
                new Edge(2, 4, 5),
                new Edge(3, 4, 5),
                new Edge(3, 5, 6),
                new Edge(4, 5, 6),
                new Edge(2, 5, 7)
        ));
    }

    /**
     * n=7, dos componentes desconectadas: {0,1,2,3} y {4,5,6}.
     * Verificado: Kruskal acepta 5 aristas en total, costo 12, termina en 2 componentes.
     */
    public static Graph level4Desconectado() {
        return new Graph(7, List.of(
                new Edge(0, 1, 2),
                new Edge(1, 2, 3),
                new Edge(2, 3, 1),
                new Edge(0, 3, 6),
                new Edge(4, 5, 2),
                new Edge(5, 6, 4),
                new Edge(4, 6, 5)
        ));
    }

    public static Graph byNumber(int level) {
        return switch (level) {
            case 1 -> level1Tutorial();
            case 2 -> level2Desafio();
            case 3 -> level3Empates();
            case 4 -> level4Desconectado();
            default -> throw new IllegalArgumentException("Nivel invalido: " + level);
        };
    }
}
