package model;

public record Edge(int u, int v, int weight) {
    public Edge {
        if (u < 0 || v < 0) throw new IllegalArgumentException("Los vertices no pueden ser negativos");
        if (u == v) throw new IllegalArgumentException("Una arista no puede ser un lazo (u == v): " + u);
    }
    public boolean touches(int vertex) { return u == vertex || v == vertex; }
    public int other(int vertex) {
        if (vertex == u) return v;
        if (vertex == v) return u;
        throw new IllegalArgumentException("El vertice " + vertex + " no pertenece a esta arista");
    }
}
