package gui;

import javafx.animation.PauseTransition;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.Edge;
import model.Graph;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Lienzo que dibuja el grafo con los vertices repartidos sobre una
 * circunferencia (disposicion polar) y las aristas coloreadas segun el estado
 * que se le informe desde afuera.
 *
 * <p><b>Esta clase no contiene ni una linea de logica algoritmica.</b> No sabe
 * que es un ciclo, no consulta un {@code DisjointSet} y no calcula ningun MST:
 * unicamente pinta los conjuntos de aristas que {@code GameApp} y
 * {@code KruskalAnimator} le entregan mediante {@link #markAccepted(Edge)},
 * {@link #flashRejected(Edge)} y {@link #highlightReplay(Edge)}. Toda decision
 * proviene de {@code game.NetworkGame} o de {@code algorithm.Kruskal}.</p>
 */
public class GraphCanvas extends Canvas {

    /** Estado visual de una arista, en orden de prioridad de pintado. */
    private enum EdgeStyle { UNSELECTED, ACCEPTED, REJECTED_FLASH, REPLAY }

    private static final Color BACKGROUND       = Color.web("#1e2530");
    private static final Color EDGE_UNSELECTED  = Color.web("#6b7683");
    private static final Color EDGE_ACCEPTED    = Color.web("#3fbf6f");
    private static final Color EDGE_REJECTED    = Color.web("#e2504a");
    private static final Color EDGE_REPLAY      = Color.web("#f0a733");
    private static final Color EDGE_HOVER       = Color.web("#9fb0c4");
    private static final Color VERTEX_FILL      = Color.web("#2f3b4c");
    private static final Color VERTEX_STROKE    = Color.web("#c8d3e0");
    private static final Color LABEL_COLOR      = Color.web("#e7edf5");
    private static final Color WEIGHT_BG        = Color.web("#151b23");

    private static final double VERTEX_RADIUS = 18;
    private static final double MARGIN        = 46;
    private static final double CLICK_TOLERANCE = 12;
    private static final Duration FLASH_DURATION = Duration.millis(650);

    private Graph graph;

    private final Set<Edge> accepted = new LinkedHashSet<>();
    private final Set<Edge> replay   = new LinkedHashSet<>();
    private final Set<Edge> rejectedFlash = new HashSet<>();
    private Edge hovered;

    private Consumer<Edge> onEdgeClicked = e -> { };

    /** Coordenadas de pantalla de cada vertice; se recalculan en cada repintado. */
    private double[] vx = new double[0];
    private double[] vy = new double[0];

    public GraphCanvas(Graph graph) {
        this.graph = graph;
        setWidth(720);
        setHeight(560);
        widthProperty().addListener(obs -> redraw());
        heightProperty().addListener(obs -> redraw());

        setOnMouseMoved(ev -> {
            Edge under = edgeAt(ev.getX(), ev.getY());
            if (under != hovered) {
                hovered = under;
                setCursor(under == null ? Cursor.DEFAULT : Cursor.HAND);
                redraw();
            }
        });
        setOnMouseExited(ev -> {
            if (hovered != null) {
                hovered = null;
                setCursor(Cursor.DEFAULT);
                redraw();
            }
        });
        setOnMouseClicked(ev -> {
            Edge target = edgeAt(ev.getX(), ev.getY());
            if (target != null) {
                onEdgeClicked.accept(target);
            }
        });

        redraw();
    }

    // ------------------------------------------------------------------
    // API que consume GameApp / KruskalAnimator (solo estado visual)
    // ------------------------------------------------------------------

    /** Registra un manejador que recibe la arista sobre la que se hizo clic. */
    public void setOnEdgeClicked(Consumer<Edge> handler) {
        this.onEdgeClicked = (handler == null) ? e -> { } : handler;
    }

    /** Cambia el grafo dibujado y limpia todo el estado visual previo. */
    public void setGraph(Graph graph) {
        this.graph = graph;
        clearAll();
    }

    /** Pinta la arista como aceptada (verde). El veredicto lo decide NetworkGame. */
    public void markAccepted(Edge e) {
        accepted.add(e);
        redraw();
    }

    /** Destello rojo temporal sobre una arista rechazada. Puro efecto visual. */
    public void flashRejected(Edge e) {
        rejectedFlash.add(e);
        redraw();
        PauseTransition fade = new PauseTransition(FLASH_DURATION);
        fade.setOnFinished(ev -> {
            rejectedFlash.remove(e);
            redraw();
        });
        fade.play();
    }

    /** Resalta una arista de la reproduccion de Kruskal (naranja). */
    public void highlightReplay(Edge e) {
        replay.add(e);
        redraw();
    }

    /** Borra el resaltado de la reproduccion, dejando intacto lo que jugo el humano. */
    public void clearReplay() {
        if (!replay.isEmpty()) {
            replay.clear();
            redraw();
        }
    }

    /** Vuelve al estado inicial: sin aceptadas, sin destellos y sin reproduccion. */
    private void clearAll() {
        accepted.clear();
        replay.clear();
        rejectedFlash.clear();
        hovered = null;
        redraw();
    }

    // ------------------------------------------------------------------
    // Redimensionado
    // ------------------------------------------------------------------

    /**
     * Devuelve un contenedor que estira el lienzo al espacio disponible.
     *
     * <p>Un {@link Canvas} no es redimensionable por si mismo, y hacerlo
     * "redimensionable" devolviendo {@code getWidth()} desde {@code prefWidth}
     * realimenta al gestor de disposicion: el lienzo crece en cada pasada de
     * layout y termina empujando al panel lateral fuera de la ventana. El
     * patron correcto es envolverlo en un {@link Pane} y atar sus dimensiones
     * a las del contenedor.</p>
     *
     * <p>El {@code Pane} necesita ademas un tamano preferido FIJO: por defecto
     * lo calcula a partir de los limites de sus hijos, asi que al atar el
     * lienzo al contenedor reaparece exactamente la misma realimentacion
     * (lienzo crece -> pref del Pane crece -> el Pane crece -> ...).</p>
     */
    public Pane resizableContainer() {
        Pane holder = new Pane(this);
        holder.setMinSize(0, 0);
        holder.setPrefSize(480, 420);
        holder.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(holder.widthProperty());
        clip.heightProperty().bind(holder.heightProperty());
        holder.setClip(clip);

        widthProperty().bind(holder.widthProperty());
        heightProperty().bind(holder.heightProperty());
        return holder;
    }

    // ------------------------------------------------------------------
    // Disposicion polar y pintado
    // ------------------------------------------------------------------

    /** Recalcula la posicion de cada vertice sobre la circunferencia. */
    private void layoutVertices() {
        int n = graph.vertexCount();
        if (vx.length != n) {
            vx = new double[n];
            vy = new double[n];
        }
        double cx = getWidth() / 2.0;
        double cy = getHeight() / 2.0;
        double radius = Math.max(40, Math.min(getWidth(), getHeight()) / 2.0 - MARGIN);

        if (n == 1) {
            vx[0] = cx;
            vy[0] = cy;
            return;
        }
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
            vx[i] = cx + radius * Math.cos(angle);
            vy[i] = cy + radius * Math.sin(angle);
        }
    }

    public void redraw() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        layoutVertices();
        GraphicsContext g = getGraphicsContext2D();
        g.setFill(BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Las aristas destacadas se pintan al final para que queden encima.
        for (Edge e : graph.edges()) {
            if (styleOf(e) == EdgeStyle.UNSELECTED) {
                drawEdge(g, e, EdgeStyle.UNSELECTED);
            }
        }
        for (Edge e : graph.edges()) {
            EdgeStyle style = styleOf(e);
            if (style != EdgeStyle.UNSELECTED) {
                drawEdge(g, e, style);
            }
        }
        for (int i = 0; i < graph.vertexCount(); i++) {
            drawVertex(g, i);
        }
    }

    private EdgeStyle styleOf(Edge e) {
        if (rejectedFlash.contains(e)) return EdgeStyle.REJECTED_FLASH;
        if (replay.contains(e))        return EdgeStyle.REPLAY;
        if (accepted.contains(e))      return EdgeStyle.ACCEPTED;
        return EdgeStyle.UNSELECTED;
    }

    private void drawEdge(GraphicsContext g, Edge e, EdgeStyle style) {
        double x1 = vx[e.u()], y1 = vy[e.u()];
        double x2 = vx[e.v()], y2 = vy[e.v()];

        Color color = switch (style) {
            case ACCEPTED       -> EDGE_ACCEPTED;
            case REJECTED_FLASH -> EDGE_REJECTED;
            case REPLAY         -> EDGE_REPLAY;
            case UNSELECTED     -> e.equals(hovered) ? EDGE_HOVER : EDGE_UNSELECTED;
        };
        double width = switch (style) {
            case UNSELECTED -> e.equals(hovered) ? 4.0 : 2.0;
            default         -> 5.0;
        };

        g.setStroke(color);
        g.setLineWidth(width);
        g.strokeLine(x1, y1, x2, y2);

        drawWeightLabel(g, (x1 + x2) / 2, (y1 + y2) / 2, e.weight(), color);
    }

    private void drawWeightLabel(GraphicsContext g, double x, double y, int weight, Color color) {
        String text = Integer.toString(weight);
        double box = 11 + 5 * text.length();
        g.setFill(WEIGHT_BG);
        g.fillRoundRect(x - box / 2, y - 9, box, 18, 8, 8);
        g.setStroke(color);
        g.setLineWidth(1.2);
        g.strokeRoundRect(x - box / 2, y - 9, box, 18, 8, 8);

        g.setFill(LABEL_COLOR);
        g.setFont(Font.font(12));
        g.setTextAlign(TextAlignment.CENTER);
        g.fillText(text, x, y + 4);
    }

    private void drawVertex(GraphicsContext g, int i) {
        g.setFill(VERTEX_FILL);
        g.fillOval(vx[i] - VERTEX_RADIUS, vy[i] - VERTEX_RADIUS, VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);
        g.setStroke(VERTEX_STROKE);
        g.setLineWidth(2);
        g.strokeOval(vx[i] - VERTEX_RADIUS, vy[i] - VERTEX_RADIUS, VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);

        g.setFill(LABEL_COLOR);
        g.setFont(Font.font(14));
        g.setTextAlign(TextAlignment.CENTER);
        g.fillText(Integer.toString(i), vx[i], vy[i] + 5);
    }

    // ------------------------------------------------------------------
    // Hit testing: arista mas cercana al punto (distancia punto-segmento)
    // ------------------------------------------------------------------

    /** Devuelve la arista bajo el punto dado, o {@code null} si no hay ninguna cerca. */
    private Edge edgeAt(double px, double py) {
        if (vx.length != graph.vertexCount()) {
            layoutVertices();
        }
        Edge best = null;
        double bestDistance = CLICK_TOLERANCE;
        for (Edge e : graph.edges()) {
            double d = distanceToSegment(px, py, vx[e.u()], vy[e.u()], vx[e.v()], vy[e.v()]);
            if (d < bestDistance) {
                bestDistance = d;
                best = e;
            }
        }
        return best;
    }

    /** Distancia euclidiana del punto (px,py) al segmento (x1,y1)-(x2,y2). */
    private static double distanceToSegment(double px, double py,
                                            double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double lengthSquared = dx * dx + dy * dy;
        if (lengthSquared == 0) {
            return Math.hypot(px - x1, py - y1);
        }
        // Proyeccion escalar del punto sobre el segmento, recortada a [0, 1].
        double t = ((px - x1) * dx + (py - y1) * dy) / lengthSquared;
        t = Math.max(0, Math.min(1, t));
        return Math.hypot(px - (x1 + t * dx), py - (y1 + t * dy));
    }
}
