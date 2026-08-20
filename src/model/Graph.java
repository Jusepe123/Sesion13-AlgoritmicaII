package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Grafo no dirigido e inmutable, representado como una lista de aristas
 * sobre un conjunto de {@code n} vertices indexados en {@code [0, n)}.
 */
public record Graph(int n, List<Edge> edges) {
    public Graph {
        if (n < 1) {
            throw new IllegalArgumentException("El grafo debe tener al menos 1 vertice (n >= 1)");
        }
        for (Edge e : edges) {
            if (e.u() < 0 || e.u() >= n || e.v() < 0 || e.v() >= n) {
                throw new IllegalArgumentException(
                        "La arista (" + e.u() + ", " + e.v() + ") referencia un vertice fuera de rango [0, " + n + ")");
            }
        }
        edges = List.copyOf(edges);
    }

    public int vertexCount() {
        return n;
    }

    /**
     * Devuelve una nueva lista de aristas ordenada por peso ascendente.
     * No modifica {@link #edges()} ni depende de ninguna clase de algoritmo.
     */
    public List<Edge> edgesSortedByWeight() {
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingInt(Edge::weight));
        return sorted;
    }

    /** Busqueda simetrica: encuentra la arista entre {@code a} y {@code b} en cualquier orden. */
    public Optional<Edge> findEdge(int a, int b) {
        for (Edge e : edges) {
            if ((e.u() == a && e.v() == b) || (e.u() == b && e.v() == a)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }
}
