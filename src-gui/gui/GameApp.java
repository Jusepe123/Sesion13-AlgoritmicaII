package gui;

import algorithm.Kruskal;
import game.GameEvaluator;
import game.GameState;
import game.GameSummary;
import game.Levels;
import game.NetworkGame;
import game.ScoreCalculator;
import game.SelectionOutcome;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Edge;
import model.Graph;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Capa grafica opcional (bonus) de NetworkBuilder.
 *
 * <p>Es estrictamente un <b>consumidor</b> del mismo motor que usa la consola:
 * toda decision de aceptar/rechazar una arista viene de
 * {@link NetworkGame#select(Edge)}, el optimo viene de
 * {@code algorithm.Kruskal} y el veredicto final de
 * {@link GameEvaluator#evaluate(NetworkGame)}. No hay ni una linea de logica de
 * ciclos ni de MST en el paquete {@code gui}: si se borrara {@code src-gui/}
 * completo, el entregable calificado (Tareas 1-4) seguiria intacto.</p>
 *
 * <p>Solo se portan los modos 1 y 2 de la consola. El modo 3 (Entrenador de
 * Kruskal) es un flujo por turnos guiado, incompatible con la interaccion
 * libre de "clic en cualquier arista" de esta ventana; sigue disponible en la
 * version de consola, que es la entregable.</p>
 */
public class GameApp extends Application {

    /** Modos portados desde la consola: cambian cuanta informacion se revela. */
    private enum Mode {
        ENTRENAMIENTO("Modo 1: Entrenamiento", true),
        DESAFIO("Modo 2: Desafio", false);

        final String label;
        /** Si es {@code false}, se ocultan componentes y se limita el detalle del log. */
        final boolean revealsComponents;

        Mode(String label, boolean revealsComponents) {
            this.label = label;
            this.revealsComponents = revealsComponents;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private record LevelChoice(int number, String name) {
        @Override
        public String toString() {
            return "Nivel " + number + " - " + name;
        }
    }

    private static final String PANEL_BG = "-fx-background-color: #232c38;";
    private static final String TEXT_FILL = "-fx-text-fill: #e7edf5;";

    /** Unica partida activa. Se reemplaza (nunca se duplica) al reiniciar o cambiar de nivel. */
    private NetworkGame game;
    private GraphCanvas canvas;
    private BorderPane root;
    private TextArea log;
    private Timeline replay;

    /** Aristas ya intentadas: evita reintentos accidentales, igual que la consola. */
    private final Set<Edge> usedEdges = new LinkedHashSet<>();

    private final IntegerProperty cost       = new SimpleIntegerProperty();
    private final IntegerProperty components = new SimpleIntegerProperty();
    private final IntegerProperty selected   = new SimpleIntegerProperty();
    private final IntegerProperty cycles     = new SimpleIntegerProperty();
    private final StringProperty  status     = new SimpleStringProperty();
    private final BooleanProperty showComponents = new SimpleBooleanProperty(true);

    private ComboBox<LevelChoice> levelBox;
    private ComboBox<Mode> modeBox;

    /** Los dos botones que revelan el optimo; se bloquean juntos (ver {@link #revealsOptimum()}). */
    private Button replayButton;
    private Button compareButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Graph initial = Levels.byNumber(1);
        game = new NetworkGame(initial);

        canvas = new GraphCanvas(initial);
        canvas.setOnEdgeClicked(this::onEdgeClicked);

        root = new BorderPane();
        root.setCenter(canvas.resizableContainer());
        root.setRight(buildSidePanel());
        root.setStyle("-fx-background-color: #1e2530;");

        refreshState();
        appendLog("Nivel 1 cargado. Haz clic sobre una arista para intentar agregarla.");

        stage.setScene(new Scene(root, 1160, 840));
        stage.setTitle("NetworkBuilder - visualizacion (bonus)");
        stage.setMinWidth(880);
        stage.setMinHeight(640);
        stage.show();
    }

    // ------------------------------------------------------------------
    // Panel lateral
    // ------------------------------------------------------------------

    private VBox buildSidePanel() {
        levelBox = new ComboBox<>();
        levelBox.getItems().addAll(
                new LevelChoice(1, "Tutorial"),
                new LevelChoice(2, "Desafio"),
                new LevelChoice(3, "Empates"),
                new LevelChoice(4, "Desconectado"));
        levelBox.getSelectionModel().selectFirst();
        levelBox.setMaxWidth(Double.MAX_VALUE);
        levelBox.setOnAction(ev -> startNewGame());

        modeBox = new ComboBox<>();
        modeBox.getItems().addAll(Mode.values());
        modeBox.getSelectionModel().selectFirst();
        modeBox.setMaxWidth(Double.MAX_VALUE);
        modeBox.setOnAction(ev -> startNewGame());

        Label costLabel = boundLabel(cost, "Costo acumulado: %d");
        Label componentsLabel = boundLabel(components, "Componentes restantes: %d");
        componentsLabel.visibleProperty().bind(showComponents);
        componentsLabel.managedProperty().bind(showComponents);
        Label selectedLabel = boundLabel(selected, "Aristas aceptadas: %d");
        Label cyclesLabel = boundLabel(cycles, "Intentos que cerraban ciclo: %d");

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(status);
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-weight: bold; " + TEXT_FILL);

        log = new TextArea();
        log.setEditable(false);
        log.setWrapText(true);
        log.setPrefRowCount(9);
        // Sin un minimo bajo, la altura minima del panel lateral no deja sitio
        // para la franja de comparacion al pie del BorderPane.
        log.setMinHeight(70);

        Button restart = new Button("Reiniciar nivel");
        restart.setMaxWidth(Double.MAX_VALUE);
        restart.setOnAction(ev -> startNewGame());

        replayButton = new Button("Animar Kruskal");
        replayButton.setMaxWidth(Double.MAX_VALUE);
        replayButton.setOnAction(ev -> playKruskalReplay());

        compareButton = new Button("Comparar con Kruskal");
        compareButton.setMaxWidth(Double.MAX_VALUE);
        compareButton.setOnAction(ev -> showComparison());

        VBox panel = new VBox(8,
                sectionTitle("Partida"),
                plainLabel("Nivel:"), levelBox,
                plainLabel("Modo:"), modeBox,
                restart,
                new Separator(),
                sectionTitle("Estado"),
                costLabel, componentsLabel, selectedLabel, cyclesLabel, statusLabel,
                new Separator(),
                sectionTitle("Bitacora"),
                log,
                new Separator(),
                replayButton, compareButton,
                scoreLegend());
        panel.setPadding(new Insets(14));
        panel.setPrefWidth(340);
        panel.setMinWidth(300);
        panel.setStyle(PANEL_BG);
        VBox.setVgrow(log, Priority.ALWAYS);
        return panel;
    }

    private Label boundLabel(IntegerProperty property, String format) {
        Label label = new Label();
        label.textProperty().bind(property.asString(format));
        label.setStyle(TEXT_FILL);
        return label;
    }

    private Label plainLabel(String text) {
        Label label = new Label(text);
        label.setStyle(TEXT_FILL);
        return label;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #9fb0c4;");
        return label;
    }

    /**
     * Leyenda de clasificaciones. Los textos se piden a
     * {@link ScoreCalculator#classify(int)} en vez de copiarlos aqui, para que
     * la GUI no duplique las reglas de puntuacion del motor.
     */
    private Label scoreLegend() {
        StringBuilder sb = new StringBuilder("Clasificaciones: ");
        int[] samples = {100, 90, 75, 60, 20};
        for (int i = 0; i < samples.length; i++) {
            if (i > 0) sb.append(" / ");
            sb.append(ScoreCalculator.classify(samples[i]));
        }
        Label label = new Label(sb.toString());
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: #7f8b99; -fx-font-size: 10px;");
        return label;
    }

    // ------------------------------------------------------------------
    // Ciclo de vida de la partida
    // ------------------------------------------------------------------

    private void startNewGame() {
        stopReplay();
        Graph graph = Levels.byNumber(levelBox.getValue().number());
        game = new NetworkGame(graph);
        usedEdges.clear();
        canvas.setGraph(graph);
        root.setBottom(null);
        log.clear();
        refreshState();
        appendLog(currentMode().label + " - " + levelBox.getValue()
                + ". Haz clic sobre una arista para intentar agregarla.");
    }

    private void onEdgeClicked(Edge edge) {
        if (isFinished()) {
            appendLog("La partida ya termino. Usa \"Reiniciar nivel\" para volver a jugar.");
            return;
        }
        if (!usedEdges.add(edge)) {
            appendLog("Esa arista ya fue utilizada. Elige otra.");
            return;
        }

        // El veredicto lo emite el motor; la GUI solo lo pinta y lo narra.
        SelectionOutcome outcome = game.select(edge);
        if (outcome.accepted()) {
            canvas.markAccepted(edge);
        } else {
            canvas.flashRejected(edge);
        }
        appendLog(describe(outcome));
        refreshState();

        if (isFinished()) {
            appendLog("Partida finalizada. Pulsa \"Comparar con Kruskal\" para ver el resultado.");
        }
    }

    private String describe(SelectionOutcome o) {
        Edge e = o.edge();
        StringBuilder sb = new StringBuilder();
        sb.append(e.u()).append(" -- ").append(e.v()).append(" (peso ").append(e.weight()).append("): ");
        sb.append(o.accepted() ? "ACEPTADA" : "RECHAZADA");

        if (currentMode().revealsComponents) {
            sb.append('\n').append(o.accepted()
                    ? "  " + e.u() + " y " + e.v() + " estaban en componentes distintos."
                    : "  " + e.u() + " y " + e.v() + " ya pertenecen al mismo componente; cerraria un ciclo.");
            sb.append("\n  Costo acumulado: ").append(o.costAfter())
              .append("  |  Componentes restantes: ").append(o.componentsAfter());
        } else {
            sb.append("\n  Costo acumulado: ").append(o.costAfter());
        }
        return sb.toString();
    }

    /** Modo activo; antes de construir el panel lateral se asume el mas informativo. */
    private Mode currentMode() {
        return (modeBox == null || modeBox.getValue() == null) ? Mode.ENTRENAMIENTO : modeBox.getValue();
    }

    /** Refleja en las propiedades observables el estado actual del motor. */
    private void refreshState() {
        cost.set(game.currentCost());
        components.set(game.remainingComponents());
        selected.set(game.selectedEdges().size());
        cycles.set(game.cycleAttempts());
        showComponents.set(currentMode().revealsComponents);
        status.set(switch (game.state()) {
            case COMPLETO -> "Red completa: todos los vertices estan conectados.";
            case EN_PROGRESO -> isFinished()
                    ? "Sin aristas disponibles: la red quedo incompleta."
                    : "En progreso.";
        });
        boolean locked = !revealsOptimum();
        if (replayButton != null)  replayButton.setDisable(locked);
        if (compareButton != null) compareButton.setDisable(locked);
    }

    /**
     * Si el jugador puede ver el optimo ahora mismo.
     *
     * <p>Vale para AMBOS botones que lo revelan: "Animar Kruskal" pinta el MST
     * sobre el lienzo y "Comparar con Kruskal" lista sus aristas y su costo en
     * el {@link ComparisonPanel}. Bloquear solo uno no sirve de nada: el otro
     * filtra exactamente la misma respuesta.</p>
     *
     * <p>En Modo Entrenamiento la informacion esta abierta desde el principio.
     * En Modo Desafio se revela recien al terminar la partida, que es tambien
     * lo que hace la consola: {@code app.Main} solo llama a
     * {@code GameEvaluator.evaluate} despues de que el modo retorna.</p>
     */
    private boolean revealsOptimum() {
        return currentMode().revealsComponents || isFinished();
    }

    /** Terminada = el motor la marco COMPLETO, o ya no quedan aristas por probar. */
    private boolean isFinished() {
        return game.state() == GameState.COMPLETO
                || usedEdges.size() >= game.graph().edges().size();
    }

    // ------------------------------------------------------------------
    // Acciones que consultan al algoritmo (nunca lo reimplementan)
    // ------------------------------------------------------------------

    private void playKruskalReplay() {
        stopReplay();
        Graph graph = game.graph();
        List<Edge> mstEdges = Kruskal.mst(graph.vertexCount(), graph.edges());
        replay = KruskalAnimator.animate(canvas, mstEdges, Duration.millis(650));
        appendLog("Reproduciendo la seleccion de Kruskal (" + mstEdges.size() + " aristas, costo "
                + Kruskal.totalWeight(mstEdges) + ").");
        replay.play();
    }

    private void stopReplay() {
        if (replay != null) {
            replay.stop();
            replay = null;
        }
        canvas.clearReplay();
    }

    private void showComparison() {
        GameSummary summary = GameEvaluator.evaluate(game);
        root.setBottom(ComparisonPanel.build(summary));
        appendLog("Resultado: " + summary.outcome()
                + "  |  Puntaje: " + (summary.score() == null ? "N/A" : summary.score()));
    }

    private void appendLog(String text) {
        log.appendText(text + "\n");
    }
}
