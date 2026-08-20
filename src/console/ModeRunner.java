package console;

import game.NetworkGame;
import model.Graph;

import java.util.Scanner;

/**
 * Contrato comun de los tres modos de juego interactivos (Entrenamiento,
 * Desafio, Kruskal Trainer). Cada implementacion construye su propio
 * {@link NetworkGame} sobre el {@link Graph} recibido, conduce la partida
 * interactivamente por consola, y devuelve el juego ya jugado para que
 * {@code app.Main} lo evalue con {@code game.GameEvaluator}.
 */
public interface ModeRunner {
    NetworkGame run(Graph graph, Scanner in);
}
