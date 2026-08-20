package app;

import console.ChallengeMode;
import console.ConsoleUI;
import console.InputReader;
import console.KruskalTrainerMode;
import console.ModeRunner;
import console.ResultPrinter;
import console.TrainingMode;
import game.GameEvaluator;
import game.GameSummary;
import game.Levels;
import game.NetworkGame;
import model.Graph;

import java.util.Scanner;

/**
 * Punto de entrada interactivo de NetworkBuilder (Tarea 4). Presenta un menu
 * de consola para elegir nivel y modo, ejecuta la partida sobre el
 * {@link ModeRunner} correspondiente, evalua el resultado con
 * {@code game.GameEvaluator} (el unico lugar autorizado a invocar Kruskal
 * fuera de este flujo de evaluacion) y lo muestra con {@code console.ResultPrinter}.
 */
public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        InputReader reader = new InputReader(in);

        ConsoleUI.banner("NetworkBuilder");
        System.out.println("Construye una red de costo minimo eligiendo aristas, una a una.");

        boolean jugarDeNuevo = true;
        while (jugarDeNuevo) {
            int nivel = reader.readInt("\nElige un nivel (1-4): ", 1, 4);
            int modo = elegirModo(reader);

            Graph graph = Levels.byNumber(nivel);
            ModeRunner runner = crearModo(modo);

            NetworkGame game = runner.run(graph, in);
            GameSummary summary = GameEvaluator.evaluate(game);
            ResultPrinter.print(summary);

            jugarDeNuevo = reader.readYesNo("\nJugar de nuevo? (s/n): ");
        }

        System.out.println("\nGracias por jugar NetworkBuilder.");
    }

    private static int elegirModo(InputReader reader) {
        System.out.println("\nModos disponibles:");
        System.out.println("  1) Entrenamiento (muestra componentes y explicacion completa)");
        System.out.println("  2) Desafio (oculta componentes)");
        System.out.println("  3) Entrenador de Kruskal (adivina aceptar/rechazar por arista)");
        return reader.readInt("Elige un modo (1-3): ", 1, 3);
    }

    private static ModeRunner crearModo(int modo) {
        return switch (modo) {
            case 1 -> new TrainingMode();
            case 2 -> new ChallengeMode();
            case 3 -> new KruskalTrainerMode();
            default -> throw new IllegalArgumentException("Modo invalido: " + modo);
        };
    }
}
