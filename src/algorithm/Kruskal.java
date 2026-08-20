package algorithm;

import dsu.DisjointSet;
import model.Edge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementacion del algoritmo de Kruskal para el arbol de expansion minima (MST).
 *
 * <p>Reutiliza {@link DisjointSet} para detectar ciclos de forma eficiente. Si el
 * grafo de entrada esta desconectado, {@link #mst(int, List)} nunca lanza una
 * excepcion: simplemente devuelve el bosque parcial que logro construir. Decidir
 * que significa un bosque incompleto para el juego es responsabilidad de otra
 * clase (GameEvaluator, en una tarea posterior), no de esta.</p>
 */
public final class Kruskal {

    private Kruskal() {}

    /**
     * Calcula el arbol de expansion minima (o bosque parcial si el grafo esta
     * desconectado) mediante Kruskal. No muta la lista {@code input} recibida.
     */
    public static List<Edge> mst(int n, List<Edge> input) {
        List<Edge> sorted = new ArrayList<>(input);
        sorted.sort(Comparator.comparingInt(Edge::weight));

        DisjointSet dsu = new DisjointSet(n);
        List<Edge> tree = new ArrayList<>();
        for (Edge e : sorted) {
            if (tree.size() == n - 1) {
                break;
            }
            if (dsu.union(e.u(), e.v())) {
                tree.add(e);
            }
        }
        return tree;
    }

    public static int totalWeight(List<Edge> tree) {
        int sum = 0;
        for (Edge e : tree) {
            sum += e.weight();
        }
        return sum;
    }

    public static boolean isSpanning(int n, List<Edge> tree) {
        return tree.size() == n - 1;
    }

    public static KruskalResult run(int n, List<Edge> edges) {
        List<Edge> t = mst(n, edges);
        return new KruskalResult(t, totalWeight(t), isSpanning(n, t));
    }
}
