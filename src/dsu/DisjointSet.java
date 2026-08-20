package dsu;

/**
 * Estructura de datos de conjuntos disjuntos (Union-Find / DSU).
 *
 * <p>Mantiene una particion de un universo de {@code n} elementos representados
 * por indices. Cada conjunto se representa con un arbol enraizado: la raiz es el
 * representante del conjunto. Las operaciones basicas son:</p>
 *
 * <ul>
 *   <li>{@code find(x)}: devuelve el representante del conjunto de {@code x} y
 *       comprime el camino recorrido.</li>
 *   <li>{@code union(a, b)}: fusiona por rango los conjuntos de {@code a} y
 *       {@code b}; devuelve {@code true} si hubo fusion y {@code false} si ya
 *       pertenecian al mismo conjunto.</li>
 * </ul>
 *
 * <p>Invariantes que se preservan:</p>
 * <ol>
 *   <li>Una raiz cumple {@code parent[r] == r}.</li>
 *   <li>Seguir repetidamente los padres desde cualquier elemento termina en una raiz.</li>
 *   <li>Dos elementos estan en el mismo conjunto si y solo si sus representantes son iguales.</li>
 *   <li>{@code union} enlaza raices; nunca fusiona "a medias" dos arboles.</li>
 * </ol>
 *
 * <p>La implementacion usa union por rango y compresion de caminos. En caso de
 * empate de rangos, se conserva como raiz al representante del primer argumento.</p>
 */
public class DisjointSet {

    private final int[] parent;
    private final int[] rank;
    private int components;

    /**
     * Crea una estructura con {@code n} conjuntos singleton: cada elemento es
     * inicialmente su propio representante.
     */
    public DisjointSet(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("El numero de elementos no puede ser negativo");
        }
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    /**
     * Devuelve el representante del conjunto de {@code x} y comprime el camino
     * recorrido (todos los nodos visitados pasan a apuntar directamente a la raiz).
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /**
     * Fusiona por rango los conjuntos de {@code a} y {@code b}.
     *
     * @return {@code true} si hubo fusion; {@code false} si ya estaban unidos.
     */
    public boolean union(int a, int b) {
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) {
            return false;
        }
        // Union por rango. En empate conserva como raiz al representante del
        // primer argumento (regla usada en la prueba de compresion).
        if (rank[ra] < rank[rb]) {
            parent[ra] = rb;
        } else {
            parent[rb] = ra;
            if (rank[ra] == rank[rb]) {
                rank[ra]++;
            }
        }
        components--;
        return true;
    }

    /**
     * Devuelve {@code true} si ambos elementos pertenecen al mismo conjunto.
     */
    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    /**
     * Devuelve el numero actual de componentes (conjuntos).
     */
    public int components() {
        return components;
    }

    /**
     * Devuelve una copia del arreglo {@code parent} (solo para trazado/evidencia).
     */
    public int[] parentArray() {
        return parent.clone();
    }
}