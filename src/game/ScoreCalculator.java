package game;

/**
 * Matematica pura de puntuacion. No depende de ninguna otra clase del paquete
 * {@code game}.
 */
public final class ScoreCalculator {

    private ScoreCalculator() {}

    public static int score(int playerCost, int mstCost, int cycleAttempts) {
        return Math.max(100 - 5 * (playerCost - mstCost) - 10 * cycleAttempts, 0);
    }

    public static String classify(int score) {
        if (score == 100) return "Ingeniero optimo";
        if (score >= 85) return "Diseno excelente";
        if (score >= 70) return "Red funcional";
        if (score >= 50) return "Red costosa";
        return "Requiere revision";
    }
}
