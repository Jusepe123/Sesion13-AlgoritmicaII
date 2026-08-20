package console;

import model.Edge;
import model.Graph;

/**
 * Constantes de color ANSI y ayudantes de formato compartidos por todos los
 * modos de juego y por {@link ResultPrinter}. No contiene logica de juego.
 *
 * <p>{@link #colorEnabled} permite degradar a texto plano si la terminal no
 * soporta secuencias ANSI (por ejemplo, algunas consolas de Windows sin
 * procesamiento de escapes habilitado). No es un requisito del enunciado,
 * pero es barato de ofrecer y mejora la robustez de la demo.</p>
 */
public final class ConsoleUI {

    public static final String ANSI_RESET = "[0m";
    public static final String ANSI_RED = "[31m";
    public static final String ANSI_GREEN = "[32m";
    public static final String ANSI_YELLOW = "[33m";
    public static final String ANSI_CYAN = "[36m";
    public static final String ANSI_BOLD = "[1m";

    /** Si es {@code false}, {@link #colorize} devuelve el texto sin adornar. */
    public static boolean colorEnabled = true;

    private ConsoleUI() {}

    public static String colorize(String text, String ansiColor) {
        if (!colorEnabled) {
            return text;
        }
        return ansiColor + text + ANSI_RESET;
    }

    public static void banner(String title) {
        String line = "=".repeat(Math.max(title.length() + 4, 20));
        System.out.println();
        System.out.println(colorize(line, ANSI_CYAN));
        System.out.println(colorize("  " + title, ANSI_CYAN + ANSI_BOLD));
        System.out.println(colorize(line, ANSI_CYAN));
    }

    public static void section(String title) {
        System.out.println();
        System.out.println(colorize("--- " + title + " ---", ANSI_YELLOW));
    }

    public static void success(String text) {
        System.out.println(colorize(text, ANSI_GREEN));
    }

    public static void failure(String text) {
        System.out.println(colorize(text, ANSI_RED));
    }

    /** Imprime la lista completa de aristas del grafo, numeradas desde 1. */
    public static void printEdgeTable(Graph graph) {
        int i = 1;
        for (Edge e : graph.edges()) {
            System.out.printf("  %2d) %d -- %d (costo %d)%n", i, e.u(), e.v(), e.weight());
            i++;
        }
    }

    public static String edgeLabel(Edge e) {
        return e.u() + " -- " + e.v() + " (costo " + e.weight() + ")";
    }
}
