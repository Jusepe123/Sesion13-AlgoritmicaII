package game;

import dsu.DisjointSet;
import model.Edge;
import model.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor del modo "jugador" del juego: el humano elige aristas una a una y
 * este motor valida cada eleccion usando exclusivamente {@link DisjointSet}.
 *
 * <p>Esta clase NUNCA debe importar ni invocar {@code algorithm.Kruskal},
 * ni siquiera indirectamente: el modo jugador no puede "jugarse solo"
 * ejecutando el algoritmo en secreto. Esa es una regla explicita del
 * enunciado y se verifica por revision de codigo.</p>
 */
public class NetworkGame {

    private final Graph graph;
    private final DisjointSet dsu;
    private final List<Edge> selected = new ArrayList<>();
    private int cost = 0;
    private int cycleAttempts = 0;
    private GameState state = GameState.EN_PROGRESO;

    public NetworkGame(Graph graph) {
        this.graph = graph;
        this.dsu = new DisjointSet(graph.vertexCount());
        if (dsu.components() == 1) {
            state = GameState.COMPLETO;
        }
    }

    // Constructor "spec-compatible": delega en el constructor principal via new Graph(n, edges).
    public NetworkGame(int n, List<Edge> edges) {
        this(new Graph(n, edges));
    }

    public SelectionOutcome select(Edge e) {
        if (!graph.edges().contains(e)) {
            throw new IllegalArgumentException("La arista " + e + " no pertenece a este grafo");
        }
        int rootU = dsu.find(e.u());
        int rootV = dsu.find(e.v());
        boolean alreadyConnected = rootU == rootV;
        boolean accepted = dsu.union(e.u(), e.v());
        if (accepted) {
            selected.add(e);
            cost += e.weight();
        } else {
            cycleAttempts++;
        }
        if (dsu.components() == 1) {
            state = GameState.COMPLETO;
        }
        return new SelectionOutcome(e, accepted, alreadyConnected, rootU, rootV, cost, dsu.components());
    }

    public boolean trySelect(Edge e) {
        return select(e).accepted();
    }

    public boolean isConnected() {
        return dsu.components() == 1;
    }

    public int currentCost() {
        return cost;
    }

    public List<Edge> selectedEdges() {
        return List.copyOf(selected);
    }

    public int remainingComponents() {
        return dsu.components();
    }

    public int cycleAttempts() {
        return cycleAttempts;
    }

    public GameState state() {
        return state;
    }

    public Graph graph() {
        return graph;
    }
}
