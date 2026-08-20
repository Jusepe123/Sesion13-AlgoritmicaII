package console;

import model.Edge;
import model.Graph;

import java.util.Optional;
import java.util.Scanner;

/**
 * Envoltorio validado sobre {@link Scanner} para toda la entrada interactiva
 * de consola. Nunca propaga una excepcion de parseo al llamador: ante una
 * entrada invalida, imprime un mensaje y vuelve a pedirla.
 */
public class InputReader {

    private final Scanner in;

    public InputReader(Scanner in) {
        this.in = in;
    }

    /**
     * Lee un entero en el rango {@code [min, max]}, re-preguntando ante
     * cualquier entrada invalida (no numerica o fuera de rango).
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextLine()) {
                // Entrada agotada (por ejemplo, stdin redirigido desde un archivo
                // que se termino): devolvemos el minimo para no colgar el programa.
                return min;
            }
            String line = in.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("  Ingresa un numero entre " + min + " y " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("  Entrada invalida: \"" + line + "\". Ingresa un numero entero.");
            }
        }
    }

    /**
     * Lee la eleccion de una arista. Acepta dos formatos de linea:
     * <ul>
     *   <li>Un unico entero: el indice (1-based) de la arista dentro de
     *       {@code graph.edges()}, tal como se muestra en {@link ConsoleUI#printEdgeTable}.</li>
     *   <li>Dos enteros separados por espacio: los extremos {@code u v} de la
     *       arista, resueltos via {@link Graph#findEdge(int, int)}.</li>
     * </ul>
     * Re-pregunta ante cualquier entrada que no resuelva a una arista existente.
     */
    public Edge readEdgeChoice(String prompt, Graph graph) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextLine()) {
                // Sin mas entrada disponible: devolvemos la primera arista del
                // grafo para no colgar el programa en una ejecucion con stdin
                // redirigido que se quedo corta.
                return graph.edges().get(0);
            }
            String line = in.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("  Entrada vacia. Ingresa el indice de una arista o \"u v\".");
                continue;
            }
            String[] parts = line.split("\\s+");
            try {
                if (parts.length == 1) {
                    int idx = Integer.parseInt(parts[0]);
                    if (idx < 1 || idx > graph.edges().size()) {
                        System.out.println("  Indice fuera de rango. Debe estar entre 1 y " + graph.edges().size() + ".");
                        continue;
                    }
                    return graph.edges().get(idx - 1);
                } else if (parts.length == 2) {
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);
                    Optional<Edge> found = graph.findEdge(u, v);
                    if (found.isEmpty()) {
                        System.out.println("  No existe una arista entre " + u + " y " + v + ".");
                        continue;
                    }
                    return found.get();
                } else {
                    System.out.println("  Formato invalido. Ingresa un indice o \"u v\".");
                }
            } catch (NumberFormatException ex) {
                System.out.println("  Entrada invalida: \"" + line + "\".");
            }
        }
    }

    /** Lee una respuesta si/no. Acepta s/si/y/yes (true) y n/no (false), sin distinguir mayusculas. */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextLine()) {
                return false;
            }
            String line = in.nextLine().trim().toLowerCase();
            switch (line) {
                case "s", "si", "y", "yes" -> {
                    return true;
                }
                case "n", "no" -> {
                    return false;
                }
                default -> System.out.println("  Responde s (si) o n (no).");
            }
        }
    }

    /** Lee A (aceptar) / R (rechazar), sin distinguir mayusculas. Devuelve true para A. */
    public boolean readAcceptReject(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextLine()) {
                return false;
            }
            String line = in.nextLine().trim().toLowerCase();
            switch (line) {
                case "a" -> {
                    return true;
                }
                case "r" -> {
                    return false;
                }
                default -> System.out.println("  Responde A (aceptar) o R (rechazar).");
            }
        }
    }
}
