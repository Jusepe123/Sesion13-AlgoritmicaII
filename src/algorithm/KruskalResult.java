package algorithm;

import model.Edge;

import java.util.List;

public record KruskalResult(List<Edge> tree, int totalWeight, boolean spanning) {}
