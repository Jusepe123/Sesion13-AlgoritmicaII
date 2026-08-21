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
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Edge;
import model.Graph;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

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

    /** Unica partida activa. Se reemplaza (nunca se duplica) al reiniciar o cambiar de nivel. */
    private NetworkGame game;
    private GraphCanvas canvas;
    private BorderPane root;
    private StackPane appShell;
    private Node welcomeScreen;
    private ListView<String> log;
    private Timeline replay;

    /** Aristas ya intentadas: evita reintentos accidentales, igual que la consola. */
    private final Set<Edge> usedEdges = new LinkedHashSet<>();

    private final IntegerProperty cost       = new SimpleIntegerProperty();
    private final IntegerProperty components = new SimpleIntegerProperty();
    private final IntegerProperty selected   = new SimpleIntegerProperty();
    private final IntegerProperty cycles     = new SimpleIntegerProperty();
    private final DoubleProperty progress    = new SimpleDoubleProperty();
    private final StringProperty progressText = new SimpleStringProperty();
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
        root.setTop(buildHeader());
        root.setCenter(buildGraphArea());
        root.setRight(buildSidePanel());
        root.getStyleClass().add("app-root");

        welcomeScreen = buildWelcomeScreen();
        appShell = new StackPane(root, welcomeScreen);

        refreshState();
        appendLog("INFO|Nivel preparado|Haz clic sobre una arista para agregarla a tu red.");

        Scene scene = new Scene(appShell, 1180, 820);
        scene.getStylesheets().add(GameApp.class.getResource("theme.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("NetworkBuilder · Laboratorio de Kruskal");
        stage.setMinWidth(880);
        stage.setMinHeight(640);
        stage.show();
    }

    private Node buildHeader() {
        Label mark = new Label("NB");
        mark.getStyleClass().add("brand-mark");
        Label title = new Label("NetworkBuilder");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("LABORATORIO INTERACTIVO DE KRUSKAL");
        subtitle.getStyleClass().add("app-subtitle");
        VBox copy = new VBox(1, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button menu = new Button("Menú principal");
        menu.getStyleClass().add("header-button");
        menu.setOnAction(ev -> showWelcomeScreen());
        HBox header = new HBox(12, mark, copy, spacer, menu);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("app-header");
        return header;
    }

    private Node buildWelcomeScreen() {
        Label eyebrow = new Label("NETWORKBUILDER");
        eyebrow.getStyleClass().add("welcome-eyebrow");
        Label title = new Label("Conecta. Optimiza. Gana.");
        title.getStyleClass().add("welcome-title");

        ImageView hero = new ImageView(new Image(
                GameApp.class.getResource("assets/network-hero.png").toExternalForm()));
        hero.setFitWidth(390);
        hero.setFitHeight(245);
        hero.setPreserveRatio(true);
        hero.setSmooth(true);
        hero.getStyleClass().add("welcome-hero");

        Button training = menuChoice("Entrenamiento", Mode.ENTRENAMIENTO);
        Button challenge = menuChoice("Desafío", Mode.DESAFIO);
        HBox choices = new HBox(12, training, challenge);
        choices.setAlignment(Pos.CENTER);

        VBox card = new VBox(12, eyebrow, title, hero, choices);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(680);
        card.getStyleClass().add("welcome-card");

        StackPane screen = new StackPane(card);
        screen.getStyleClass().add("welcome-screen");
        return screen;
    }

    private Button menuChoice(String title, Mode mode) {
        Button button = new Button(title);
        button.setWrapText(true);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add(mode == Mode.ENTRENAMIENTO ? "menu-primary" : "menu-secondary");
        button.setOnAction(ev -> enterGame(mode));
        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    private void enterGame(Mode mode) {
        modeBox.getSelectionModel().select(mode);
        startNewGame();
        FadeTransition fade = new FadeTransition(Duration.millis(260), welcomeScreen);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(ev -> {
            welcomeScreen.setVisible(false);
            welcomeScreen.setMouseTransparent(true);
        });
        fade.play();
    }

    private void showWelcomeScreen() {
        stopReplay();
        welcomeScreen.setOpacity(0);
        welcomeScreen.setVisible(true);
        welcomeScreen.setMouseTransparent(false);
        FadeTransition fade = new FadeTransition(Duration.millis(220), welcomeScreen);
        fade.setToValue(1);
        fade.play();
    }

    private Node buildGraphArea() {
        Label hint = new Label("Selecciona una arista para conectarla a tu red");
        hint.getStyleClass().add("canvas-hint");
        HBox legend = new HBox(16,
                legendItem("legend-ready", "Disponible"),
                legendItem("legend-accepted", "Aceptada"),
                legendItem("legend-replay", "Kruskal"));
        legend.getStyleClass().add("graph-legend");

        ProgressBar completion = new ProgressBar();
        completion.progressProperty().bind(progress);
        completion.setPrefWidth(120);
        completion.getStyleClass().add("network-progress");
        Label completionText = new Label();
        completionText.textProperty().bind(progressText);
        completionText.getStyleClass().add("progress-label");
        VBox progressBox = new VBox(3, completionText, completion);
        progressBox.setAlignment(Pos.CENTER_RIGHT);

        HBox graphHeader = new HBox(16, hint, legend, progressBox);
        graphHeader.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(hint, Priority.ALWAYS);
        graphHeader.getStyleClass().add("graph-header");

        Node drawingArea = canvas.resizableContainer();
        VBox.setVgrow(drawingArea, Priority.ALWAYS);
        VBox area = new VBox(graphHeader, drawingArea);
        area.getStyleClass().add("graph-card");
        BorderPane.setMargin(area, new Insets(18));
        return area;
    }

    private Node legendItem(String colorClass, String text) {
        Label dot = new Label();
        dot.getStyleClass().addAll("legend-dot", colorClass);
        Label label = new Label(text);
        label.getStyleClass().add("legend-label");
        HBox item = new HBox(6, dot, label);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    // ------------------------------------------------------------------
    // Panel lateral
    // ------------------------------------------------------------------

    private Node buildSidePanel() {
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

        Node costLabel = metricCard("COSTO", cost);
        Node componentsLabel = metricCard("COMPONENTES", components);
        componentsLabel.visibleProperty().bind(showComponents);
        componentsLabel.managedProperty().bind(showComponents);
        Node selectedLabel = metricCard("ACEPTADAS", selected);
        Node cyclesLabel = metricCard("CICLOS", cycles);

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(status);
        statusLabel.setWrapText(true);
        statusLabel.getStyleClass().add("status-label");

        log = new ListView<>();
        log.setCellFactory(view -> new ActivityCell());
        log.setPlaceholder(new Label("Aún no hay actividad"));
        log.getStyleClass().add("activity-log");
        log.setPrefHeight(210);
        // Sin un minimo bajo, la altura minima del panel lateral no deja sitio
        // para la franja de comparacion al pie del BorderPane.
        log.setMinHeight(70);

        Button restart = new Button("Reiniciar nivel");
        restart.setMaxWidth(Double.MAX_VALUE);
        restart.getStyleClass().add("secondary-button");
        restart.setOnAction(ev -> startNewGame());

        replayButton = new Button("Animar Kruskal");
        replayButton.setMaxWidth(Double.MAX_VALUE);
        replayButton.getStyleClass().add("secondary-button");
        replayButton.setOnAction(ev -> playKruskalReplay());

        compareButton = new Button("Comparar con Kruskal");
        compareButton.setMaxWidth(Double.MAX_VALUE);
        compareButton.getStyleClass().add("primary-button");
        compareButton.setOnAction(ev -> showComparison());

        VBox panel = new VBox(8,
                sectionTitle("Partida"),
                plainLabel("Nivel:"), levelBox,
                plainLabel("Modo:"), modeBox,
                restart,
                new Separator(),
                sectionTitle("Estado"),
                new HBox(8, costLabel, componentsLabel),
                new HBox(8, selectedLabel, cyclesLabel), statusLabel,
                new Separator(),
                sectionTitle("Bitacora"),
                log,
                new Separator(),
                replayButton, compareButton,
                scoreLegend());
        panel.setPadding(new Insets(18));
        panel.setPrefWidth(350);
        panel.setMinWidth(320);
        panel.getStyleClass().add("side-panel");
        VBox.setVgrow(log, Priority.ALWAYS);

        ScrollPane scroller = new ScrollPane(panel);
        scroller.setFitToWidth(true);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.setPrefWidth(350);
        scroller.setMinWidth(320);
        scroller.getStyleClass().add("side-scroller");
        return scroller;
    }

    private Node metricCard(String caption, IntegerProperty property) {
        Label value = new Label();
        value.textProperty().bind(property.asString());
        value.getStyleClass().add("metric-value");
        Label name = new Label(caption);
        name.getStyleClass().add("metric-caption");
        VBox card = new VBox(1, value, name);
        card.getStyleClass().add("metric-card");
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label plainLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
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
        label.getStyleClass().add("score-legend");
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
        animateGraphEntrance();
        root.setBottom(null);
        log.getItems().clear();
        refreshState();
        appendLog("INFO|Nueva partida|" + currentMode().label + " · " + levelBox.getValue()
                + "\nSelecciona una arista para comenzar.");
    }

    private void onEdgeClicked(Edge edge) {
        if (isFinished()) {
            appendLog("INFO|Partida finalizada|Reinicia el nivel para volver a jugar.");
            return;
        }
        if (!usedEdges.add(edge)) {
            appendLog("WARN|Arista ya utilizada|Elige una conexión diferente.");
            return;
        }

        // El veredicto lo emite el motor; la GUI solo lo pinta y lo narra.
        SelectionOutcome outcome = game.select(edge);
        if (outcome.accepted()) {
            canvas.markAccepted(edge);
            ScaleTransition pulse = new ScaleTransition(Duration.millis(150), canvas);
            pulse.setToX(1.008);
            pulse.setToY(1.008);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            pulse.play();
        } else {
            canvas.flashRejected(edge);
        }
        appendLog(describe(outcome));
        refreshState();

        if (isFinished()) {
            appendLog("SUCCESS|Red finalizada|Ya puedes comparar tu solución con Kruskal.");
            if (game.state() == GameState.COMPLETO) {
                playCompletionCelebration();
            }
        }
    }

    private String describe(SelectionOutcome o) {
        Edge e = o.edge();
        StringBuilder sb = new StringBuilder();
        sb.append(o.accepted() ? "SUCCESS|Arista aceptada" : "ERROR|Arista rechazada");
        sb.append('|').append(e.u()).append(" ↔ ").append(e.v()).append("  ·  Peso ").append(e.weight());

        if (currentMode().revealsComponents) {
            sb.append("\n").append(o.accepted()
                    ? "Conecta dos componentes diferentes."
                    : "Crearía un ciclo porque ambos vértices ya están conectados.");
            sb.append("\nCosto: ").append(o.costAfter())
              .append("  ·  Componentes: ").append(o.componentsAfter());
        } else {
            sb.append("\nCosto acumulado: ").append(o.costAfter());
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
        int targetEdges = Math.max(1, game.graph().vertexCount() - 1);
        progress.set(Math.min(1.0, game.selectedEdges().size() / (double) targetEdges));
        progressText.set(game.selectedEdges().size() + " / " + targetEdges + " conexiones");
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
        appendLog("INFO|Animación de Kruskal|" + mstEdges.size() + " aristas · Costo óptimo "
                + Kruskal.totalWeight(mstEdges));
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
        Node panel = ComparisonPanel.build(summary);
        root.setBottom(panel);
        FadeTransition fade = new FadeTransition(Duration.millis(260), panel);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(260), panel);
        slide.setFromY(28);
        slide.setToY(0);
        new ParallelTransition(fade, slide).play();
        appendLog("INFO|Comparación lista|Resultado: " + summary.outcome()
                + " · Puntaje: " + (summary.score() == null ? "N/A" : summary.score()));
    }

    private void appendLog(String text) {
        log.getItems().add(text);
        log.scrollTo(log.getItems().size() - 1);
    }

    private void animateGraphEntrance() {
        canvas.setOpacity(0);
        canvas.setScaleX(.96);
        canvas.setScaleY(.96);
        FadeTransition fade = new FadeTransition(Duration.millis(320), canvas);
        fade.setToValue(1);
        ScaleTransition scale = new ScaleTransition(Duration.millis(320), canvas);
        scale.setToX(1);
        scale.setToY(1);
        new ParallelTransition(fade, scale).play();
    }

    /** Breve celebracion visual; no participa en el estado ni en la puntuacion. */
    private void playCompletionCelebration() {
        Pane layer = new Pane();
        layer.setMouseTransparent(true);
        layer.setPickOnBounds(false);
        layer.prefWidthProperty().bind(appShell.widthProperty());
        layer.prefHeightProperty().bind(appShell.heightProperty());
        appShell.getChildren().add(layer);

        Random random = new Random();
        ParallelTransition celebration = new ParallelTransition();
        Color[] colors = {
                Color.web("#62d9b7"), Color.web("#4dbce9"),
                Color.web("#ffbd5b"), Color.web("#dce7f2")
        };
        double width = Math.max(700, appShell.getWidth());
        for (int i = 0; i < 34; i++) {
            Rectangle piece = new Rectangle(5 + random.nextDouble() * 7,
                    3 + random.nextDouble() * 5, colors[i % colors.length]);
            piece.setArcWidth(3);
            piece.setArcHeight(3);
            piece.setLayoutX(random.nextDouble() * width);
            piece.setLayoutY(-20 - random.nextDouble() * 100);
            piece.setRotate(random.nextDouble() * 180);
            layer.getChildren().add(piece);

            Duration duration = Duration.millis(900 + random.nextInt(700));
            TranslateTransition fall = new TranslateTransition(duration, piece);
            fall.setByX(-55 + random.nextDouble() * 110);
            fall.setByY(Math.max(650, appShell.getHeight() + 140));
            RotateTransition spin = new RotateTransition(duration, piece);
            spin.setByAngle(240 + random.nextDouble() * 480);
            FadeTransition fade = new FadeTransition(duration, piece);
            fade.setDelay(Duration.millis(350));
            fade.setFromValue(.9);
            fade.setToValue(0);
            celebration.getChildren().add(new ParallelTransition(fall, spin, fade));
        }
        celebration.setOnFinished(ev -> appShell.getChildren().remove(layer));
        celebration.play();
    }

    /** Tarjeta compacta para cada evento de la bitácora. */
    private static final class ActivityCell extends ListCell<String> {
        private final Label message = new Label();

        ActivityCell() {
            message.setWrapText(true);
            message.maxWidthProperty().bind(widthProperty().subtract(28));
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            getStyleClass().removeAll("log-success", "log-error", "log-warn", "log-info");
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            String[] parts = item.split("\\|", 3);
            String type = parts.length == 3 ? parts[0] : "INFO";
            String title = parts.length == 3 ? parts[1] : "Actividad";
            String detail = parts.length == 3 ? parts[2] : item;
            String icon = switch (type) {
                case "SUCCESS" -> "✓  ";
                case "ERROR" -> "×  ";
                case "WARN" -> "!  ";
                default -> "•  ";
            };
            message.setText(icon + title + "\n" + detail);
            getStyleClass().add(switch (type) {
                case "SUCCESS" -> "log-success";
                case "ERROR" -> "log-error";
                case "WARN" -> "log-warn";
                default -> "log-info";
            });
            setGraphic(message);
        }
    }
}
