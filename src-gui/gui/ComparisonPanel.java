package gui;

import game.GameSummary;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Edge;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel de comparacion jugador vs. Kruskal.
 *
 * <p>Solo formatea campos que {@code game.GameEvaluator} ya calculo dentro de
 * un {@link GameSummary}: no vuelve a correr Kruskal, no recalcula costos y no
 * decide el veredicto. Cuando {@code score}/{@code scoreClassification} vienen
 * en {@code null} (partida imposible o incompleta) muestra "N/A" en vez de
 * inventar un numero, igual que hace {@code console.ResultPrinter}.</p>
 */
public final class ComparisonPanel {

    private static final String OK_COLOR      = "#3fbf6f";
    private static final String WARN_COLOR    = "#f0a733";
    private static final String BAD_COLOR     = "#e2504a";
    private static final String NEUTRAL_COLOR = "#c8d3e0";

    private ComparisonPanel() {}

    /** Construye el nodo listo para insertarse en la escena. */
    public static Node build(GameSummary summary) {
        Label title = new Label("Comparacion con Kruskal");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + NEUTRAL_COLOR + ";");

        HBox columns = new HBox(14,
                column("Tu red", summary.playerEdges(), summary.playerCost(), OK_COLOR),
                column("MST de Kruskal", summary.mstEdges(), summary.mstCost(), WARN_COLOR));
        HBox.setHgrow(columns.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(columns.getChildren().get(1), Priority.ALWAYS);

        // Veredicto y puntaje van arriba: son lo que el jugador vino a leer, y
        // asi siguen visibles aunque la franja inferior quede corta y haya que
        // desplazar las listas de aristas.
        VBox content = new VBox(10, title, verdict(summary), scoreLine(summary), columns);
        content.setPadding(new Insets(14));
        content.setStyle("-fx-background-color: #232c38;");

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(240);
        scroller.setMinHeight(120);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setStyle("-fx-background-color: #232c38; -fx-border-color: #3a4757; -fx-border-width: 1 0 0 0;");
        return scroller;
    }

    private static VBox column(String heading, List<Edge> edges, int totalCost, String accent) {
        Label header = new Label(heading);
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: " + accent + ";");

        ListView<String> list = new ListView<>();
        list.getItems().setAll(edges.stream().map(ComparisonPanel::edgeLabel).collect(Collectors.toList()));
        // Altura acotada: el panel vive en la franja inferior del BorderPane y
        // debe caber sin empujar al lienzo fuera de la ventana.
        list.setPrefHeight(104);
        list.setMinHeight(64);
        list.setPlaceholder(new Label("(ninguna arista)"));

        Label total = new Label("Aristas: " + edges.size() + "   |   Costo total: " + totalCost);
        total.setStyle("-fx-text-fill: " + NEUTRAL_COLOR + ";");

        VBox box = new VBox(6, header, list, total);
        VBox.setVgrow(list, Priority.ALWAYS);
        return box;
    }

    private static Label verdict(GameSummary summary) {
        String text = switch (summary.outcome()) {
            case OPTIMAL -> "EXCELENTE: tu red es un MST (costo optimo " + summary.mstCost() + ").";
            case FEASIBLE_NOT_OPTIMAL -> "RED COMPLETA, PERO NO OPTIMA: diferencia +"
                    + (summary.playerCost() - summary.mstCost()) + " sobre el optimo.";
            case INCOMPLETE -> "PARTIDA INCOMPLETA: la red aun no conecta todos los vertices.";
            case IMPOSSIBLE -> "No existe un MST global: el grafo es desconectado.";
        };
        String color = switch (summary.outcome()) {
            case OPTIMAL -> OK_COLOR;
            case FEASIBLE_NOT_OPTIMAL -> WARN_COLOR;
            case INCOMPLETE, IMPOSSIBLE -> BAD_COLOR;
        };
        Label label = new Label(text);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + ";");
        return label;
    }

    private static Label scoreLine(GameSummary summary) {
        String score = (summary.score() == null) ? "N/A" : summary.score().toString();
        String classification = (summary.scoreClassification() == null) ? "N/A" : summary.scoreClassification();
        Label label = new Label("Puntaje: " + score + "   |   Clasificacion: " + classification);
        label.setStyle("-fx-text-fill: " + NEUTRAL_COLOR + ";");
        return label;
    }

    private static String edgeLabel(Edge e) {
        return e.u() + " -- " + e.v() + "  (peso " + e.weight() + ")";
    }
}
