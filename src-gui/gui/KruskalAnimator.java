package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.Edge;

import java.util.List;

/**
 * Reproduce visualmente el resultado de Kruskal: recibe la lista de aristas
 * que {@code algorithm.Kruskal.mst(...)} ya devolvio (en orden de aceptacion)
 * y arma un {@link Timeline} que enciende una arista por {@link KeyFrame}.
 *
 * <p>Es puro <i>playback</i>. No ordena aristas, no detecta ciclos y no decide
 * cual arista entra al MST: ese trabajo ya lo hizo {@code algorithm.Kruskal}.
 * Reordenar o filtrar aqui seria duplicar el algoritmo, cosa que el enunciado
 * prohibe explicitamente.</p>
 */
public final class KruskalAnimator {

    private KruskalAnimator() {}

    /**
     * Construye la animacion de reproduccion.
     *
     * @param canvas        lienzo que recibira los resaltados
     * @param mstEdges      aristas del MST en el orden en que Kruskal las acepto
     * @param stepDuration  tiempo entre una arista y la siguiente
     * @return un {@link Timeline} detenido, listo para {@code play()}
     */
    public static Timeline animate(GraphCanvas canvas, List<Edge> mstEdges, Duration stepDuration) {
        Timeline timeline = new Timeline();
        if (canvas == null || mstEdges == null) {
            return timeline;
        }
        Duration step = (stepDuration == null || stepDuration.lessThanOrEqualTo(Duration.ZERO))
                ? Duration.millis(600)
                : stepDuration;

        // Fotograma 0: limpia el resaltado anterior para que la animacion se
        // pueda volver a reproducir tantas veces como el jugador quiera.
        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, ev -> canvas.clearReplay()));

        for (int i = 0; i < mstEdges.size(); i++) {
            Edge edge = mstEdges.get(i);
            timeline.getKeyFrames().add(
                    new KeyFrame(step.multiply(i + 1), ev -> canvas.highlightReplay(edge)));
        }
        return timeline;
    }
}
